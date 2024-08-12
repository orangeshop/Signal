package com.ssafy.signal.member.jwt.json;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.http.HttpStatus;

@Getter
@ToString
@NoArgsConstructor
public class ApiResponseJson {
    public HttpStatus httpStatus;
    public int code;
    public Object data;

    public ApiResponseJson(HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
        this.code = code;
        this.data = data;
    }

    public ApiResponseJson(HttpStatus httpStatus, int code) {
        this.httpStatus = httpStatus;
        this.code = code;
        this.data = data;
    }

    public ApiResponseJson(HttpStatus httpStatus, Object data) {
        this.httpStatus = httpStatus;
        this.code = ResponseStatusCode.OK;
        this.data = data;
    }
}
