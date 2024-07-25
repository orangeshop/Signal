package com.ssafy.signal.notification.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class FcmMessage {
    private boolean validate_only;
    private Message message;

    @Getter
    @Setter
    @AllArgsConstructor
    public static class Message{
        private Data data;
        private String token;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    public static class Data{
        private String title;
        private String body;
    }
}
