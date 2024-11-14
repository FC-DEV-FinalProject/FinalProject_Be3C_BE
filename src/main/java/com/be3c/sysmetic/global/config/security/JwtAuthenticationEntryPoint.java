package com.be3c.sysmetic.global.config.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;

@NoArgsConstructor
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
    // 인증이 실패할 경우 예외 처리하는 클래스
    /*
        AuthenticationEntryPoint 인터페이스에서 필수 구현해야 하는 메서드. 인증되지 않은 사용자가 보호된 리소스에 접근할 때 사용.
        AuthenticationException authException : 인증 과정에서 발생한 예외로, 예외 정보를 활용하여 더 구체적인 오류 메시지를 제공할 수 있다.
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized") : 401 Unauthorized 상태 코드를 반환하여, 클라이언트에게 인증이 필요하다는 메시지를 전달.
        SC_UNAUTHORIZED : HTTP 상태 코드 401을 의미. 사용자가 인증되지 않았음을 알림
        "Unauthorized" : 클라이언트에게 전송될 메시지
    */
    @Override
    public void commence(final HttpServletRequest request, final HttpServletResponse response, AuthenticationException authException) throws IOException {
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
    }
}
