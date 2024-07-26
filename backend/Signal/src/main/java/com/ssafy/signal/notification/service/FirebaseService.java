package com.ssafy.signal.notification.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.auth.oauth2.GoogleCredentials;

import com.ssafy.signal.notification.domain.FcmMessage.Message;
import com.ssafy.signal.notification.domain.FcmMessage;
import jakarta.annotation.PostConstruct;
import okhttp3.*;
import org.springdoc.core.providers.ObjectMapperProvider;
import org.springframework.http.HttpHeaders;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.IOException;

@Service
public class FirebaseService {
    private final String API_URL = "https://fcm.googleapis.com/v1/projects/signal-d51bd/messages:send";
    public final ObjectMapperProvider objectMapperProvider;
    private String accessToken;

    public FirebaseService(ObjectMapperProvider objectMapperProvider) {
        this.objectMapperProvider = objectMapperProvider;

    }

    @PostConstruct
    public void init() throws IOException {
        getAccessToken();
    }

    @Scheduled(fixedRate = 3000000)  // 50분(3000초)마다 갱신
    public void getAccessToken() throws IOException {
        FileInputStream serviceAccount = new FileInputStream("src/main/resources/firebase/firebase_service_key.json");
        GoogleCredentials googleCredentials = GoogleCredentials.fromStream(serviceAccount)
                .createScoped("https://www.googleapis.com/auth/cloud-platform");
        googleCredentials.refreshIfExpired();
        accessToken = googleCredentials.getAccessToken().getTokenValue();
    }

    public String getCurrentAccessToken() {
        return accessToken;
    }

    public String sendMessageTo(String targetToken,String title, String body) throws IOException
    {
        String message = makeMessage(targetToken,title,body);
        System.out.println("msg 내용 : "+message);
        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = RequestBody.create(message, MediaType.get("application/json;charset=utf-8"));
        Request request = new Request.Builder()
                .url(API_URL)
                .post(requestBody)
                .addHeader(HttpHeaders.AUTHORIZATION,"Bearer " + getCurrentAccessToken())
                .addHeader(HttpHeaders.CONTENT_TYPE,"application/json;UTF-8")
                .build();
        Response response = client.newCall(request).execute();

        System.out.println(response.body().string());
        return message;
    }

    private String makeMessage(String targetToken, String title, String body) throws JsonProcessingException {
        FcmMessage.Data data = new FcmMessage.Data("from data " + title,body);
        Message message = new FcmMessage.Message(data,targetToken);
        FcmMessage fcmMessage = new FcmMessage(false,message);
        return objectMapperProvider.jsonMapper().writeValueAsString(fcmMessage);
    }
}