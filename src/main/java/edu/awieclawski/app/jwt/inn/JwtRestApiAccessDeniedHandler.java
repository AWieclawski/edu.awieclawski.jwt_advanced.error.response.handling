package edu.awieclawski.app.jwt.inn;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.DependsOn;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Serializable;

@Slf4j
@Component(value = JwtRestApiAccessDeniedHandler.BEAN_NAME)
@RequiredArgsConstructor
@DependsOn(JwtMessageHelper.BEAN_NAME)
class JwtRestApiAccessDeniedHandler implements AccessDeniedHandler, Serializable {

    static final String BEAN_NAME = "edu.awieclawski.app.jwt.inn.JwtRestApiAccessDeniedHandler";

    private static final long serialVersionUID = -345869558953243863L;

    private final JwtMessageHelper messageHelper;

    @Override
    public void handle(
            HttpServletRequest request,
            HttpServletResponse response,
            AccessDeniedException authException) throws IOException, ServletException {

        Authentication auth
                = SecurityContextHolder.getContext().getAuthentication();
        String userLogin = null;
        if (auth != null) {
            userLogin = auth.getName();
            log.warn("Access denied for User: {} attempted to access the protected URL: {}", userLogin, request.getRequestURI());
        }

        HttpStatus httpStatus = getHttpStatus(authException);
        response.setStatus(getHttpResponse(authException));
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        String message;

        if (authException.getCause() != null) {
            message = authException.getCause().getMessage() + " " + authException.getMessage();
        } else {
            message = authException.getMessage();
        }

        messageHelper.handleMessage(httpStatus, message, request, response, authException, userLogin);
    }

    private HttpStatus getHttpStatus(Exception exception) {
        log.debug("getHttpStatus ref. Exception: {}", exception.getClass().getSimpleName());
        return HttpStatus.FORBIDDEN;
    }

    private Integer getHttpResponse(Exception exception) {
        log.debug("getHttpResponse ref. Exception: {}", exception.getClass().getSimpleName());
        return HttpServletResponse.SC_FORBIDDEN;
    }
}
