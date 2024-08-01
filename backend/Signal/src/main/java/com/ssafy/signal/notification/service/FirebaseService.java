package com.ssafy.signal.notification.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.auth.oauth2.GoogleCredentials;

import com.ssafy.signal.notification.domain.FcmMessage.Message;
import com.ssafy.signal.notification.domain.FcmMessage;
import com.ssafy.signal.notification.domain.TokenResponse;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springdoc.core.providers.ObjectMapperProvider;
import org.springframework.http.HttpHeaders;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class FirebaseService {
    private final String API_URL = "https://fcm.googleapis.com/v1/projects/signal-d51bd/messages:send";
    public final ObjectMapperProvider objectMapperProvider;
    private String accessToken;

    Map<Long,String> userTokens = new ConcurrentHashMap<>();

    public FirebaseService(ObjectMapperProvider objectMapperProvider) {
        this.objectMapperProvider = objectMapperProvider;

    }

    @PostConstruct
    public void init() throws IOException {
        try {
            getAccessToken();
        } catch (IOException e) {
            e.printStackTrace();
            // 로그 출력 또는 적절한 예외 처리
        }
    }

    @Scheduled(fixedRate = 1000000)  // 50분(3000초)마다 갱신
    public void getAccessToken() throws IOException {
        String prefix = "backend/Signal/";
        String path = "backend/Signal/src/main/resources/firebase/firebase_service_key.json";

        boolean isRemote = true;
        path = isRemote ? path : prefix + path;

        FileInputStream serviceAccount = new FileInputStream("backend/Signal/src/main/resources/firebase/firebase_service_key.json");
        GoogleCredentials googleCredentials = GoogleCredentials.fromStream(serviceAccount)
                .createScoped("https://www.googleapis.com/auth/cloud-platform");
        googleCredentials.refreshIfExpired();
        accessToken = googleCredentials.getAccessToken().getTokenValue();
    }

    public String getCurrentAccessToken() {
        return accessToken;
    }

    public String sendMessageTo(long user_id,String title, String body) throws IOException
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

        log.info(response.body().string());
        return message;
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
}
