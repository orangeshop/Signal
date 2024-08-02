package com.ssafy.signal.notification.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.auth.oauth2.GoogleCredentials;

import com.ssafy.signal.notification.domain.FcmMessage.Message;
import com.ssafy.signal.notification.domain.FcmMessage;
import com.ssafy.signal.notification.domain.NotiFailEntity;
import com.ssafy.signal.notification.domain.TokenResponse;
import com.ssafy.signal.notification.repository.NotiFailRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springdoc.core.providers.ObjectMapperProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpHeaders;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RequiredArgsConstructor
@Slf4j
@Service
public class FirebaseService {
    private final String API_URL = "https://fcm.googleapis.com/v1/projects/signal-d51bd/messages:send";
    public final ObjectMapperProvider objectMapperProvider;
    private String accessToken;
    private final ResourceLoader resourceLoader;
    private final NotiFailRepository notiFailRepository;

    Map<Long,String> userTokens = new ConcurrentHashMap<>();


    @PostConstruct
    public void init() throws IOException {
        try {
            getAccessToken();
        } catch (IOException e) {
            e.printStackTrace();
            // 로그 출력 또는 적절한 예외 처리
        }
    }

    @Scheduled(fixedRate = 3000000)  // 50분(3000초)마다 갱신
    public void getAccessToken() throws IOException {
        Resource resource = resourceLoader.getResource("classpath:firebase/firebase_service_key.json");
        InputStream inputStream = resource.getInputStream();

        GoogleCredentials googleCredentials = GoogleCredentials.fromStream(inputStream)
                .createScoped("https://www.googleapis.com/auth/cloud-platform");
        googleCredentials.refreshIfExpired();
        accessToken = googleCredentials.getAccessToken().getTokenValue();
        log.info("Access token: " + accessToken);
    }

    public String getCurrentAccessToken() {
        return accessToken;
    }

    public Response sendMessageTo(long user_id,String title, String body,int cnt) throws IOException
    {
        String targetToken = userTokens.get(user_id);
        if(targetToken == null) return null;
        String message = makeMessage(targetToken,title,body);
        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = RequestBody.create(message, MediaType.get("application/json;charset=utf-8"));
        Request request = new Request.Builder()
                .url(API_URL)
                .post(requestBody)
                .addHeader(HttpHeaders.AUTHORIZATION,"Bearer " + getCurrentAccessToken())
                .addHeader(HttpHeaders.CONTENT_TYPE,"application/json;UTF-8")
                .build();
        Response response = client.newCall(request).execute();

        if(response.code() >= 400) {
            log.error(response.body().string());
            getAccessToken();
            saveFailNoti(user_id,title,body,cnt+1);
        }
        else log.info(response.body().string());
        return response;
    }

    private String makeMessage(String targetToken, String title, String body) throws JsonProcessingException {
        FcmMessage.Data data = new FcmMessage.Data(title,body);
        Message message = new FcmMessage.Message(data,targetToken);
        FcmMessage fcmMessage = new FcmMessage(false,message);
        return objectMapperProvider.jsonMapper().writeValueAsString(fcmMessage);
    }

    public TokenResponse registToken(long user_id, String token) {
        userTokens.put(user_id,token);
        log.info("User "+user_id+" has been registered.");
        return TokenResponse.builder().user_id(user_id).token(token).build();
    }


    private void saveFailNoti(long user_id, String title, String body,int cnt) {
        notiFailRepository.save(
                NotiFailEntity.builder()
                        .userId(user_id)
                        .token(userTokens.getOrDefault(user_id,""))
                        .title(title)
                        .body(body)
                        .failureCount(cnt)
                        .build()
        );
    }

    @Scheduled(fixedRate = 1000 * 20)
    private void handleErrorNotification() throws IOException {
        List<NotiFailEntity> notiFails = notiFailRepository.findAll();

        for(NotiFailEntity event : notiFails) {
            if(event.getFailureCount() > 10)
            {
                notiFailRepository.delete(event);
                continue;
            }

            Response response = sendMessageTo(event.getUserId(), event.getTitle(),
                    event.getBody(), event.getFailureCount());

            if(response == null) continue;
            if(response.code() >= 200 && response.code() < 300) {
                notiFailRepository.delete(event);
            }
        }
    }
}
