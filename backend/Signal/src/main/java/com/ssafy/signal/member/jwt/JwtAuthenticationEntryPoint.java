package com.ssafy.signal.member.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssafy.signal.member.jwt.json.ApiResponseJson;
import com.ssafy.signal.member.jwt.json.ResponseStatusCode;
import com.ssafy.signal.member.jwt.token.TokenStatus;
import com.ssafy.signal.member.jwt.token.dto.TokenValidationResult;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;
import java.util.Map;

@Slf4j
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private static final String VALIDATION_RESULT_KEY = "result";
    private static final String ERROR_MESSAGE_KEY = "errMsg";
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        TokenValidationResult result = (TokenValidationResult) request.getAttribute(VALIDATION_RESULT_KEY);
        String errorMessage = result.getTokenStatus().getMessage();
        int errorCode;

        switch (result.getTokenStatus()) {
            case TOKEN_EXPIRED -> errorCode = ResponseStatusCode.TOKEN_EXPIRED;
            case TOKEN_IS_BLACKLIST -> errorCode = ResponseStatusCode.TOKEN_IS_BLACKLIST;
            case TOKEN_WRONG_SIGNATURE -> errorCode = ResponseStatusCode.TOKEN_WRONG_SIGNATURE;
            case TOKEN_HASH_NOT_SUPPORTED -> errorCode = ResponseStatusCode.TOKEN_HASH_NOT_SUPPORTED;
            case WRONG_AUTH_HEADER -> errorCode = ResponseStatusCode.NO_AUTH_HEADER;
            default -> {
                errorMessage = TokenStatus.TOKEN_VALIDATION_TRY_FAILED.getMessage();
                errorCode = ResponseStatusCode.TOKEN_VALIDATION_TRY_FAILED;
            }
        }
        sendError(response, errorMessage, errorCode);
    }

    private void sendError(HttpServletResponse response, String errorMessage, int errorCode) throws IOException{
        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        ApiResponseJson responseJson = new ApiResponseJson(HttpStatus.valueOf(HttpServletResponse.SC_UNAUTHORIZED),
                errorCode, Map.of(ERROR_MESSAGE_KEY, errorMessage));

        String JsonToString = objectMapper.writeValueAsString(responseJson);
        response.getWriter().write(JsonToString);
    }
}
