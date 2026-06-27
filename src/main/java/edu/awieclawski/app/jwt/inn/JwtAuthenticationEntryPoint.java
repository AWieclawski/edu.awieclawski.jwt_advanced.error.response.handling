package edu.awieclawski.app.jwt.inn;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.DependsOn;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Serializable;

/**
 * https://codingtechroom.com/question/java-lang-illegalstateexception-getwriter-already-called-servlet
 */
@Slf4j
@Component(value = JwtAuthenticationEntryPoint.BEAN_NAME)
@RequiredArgsConstructor
@DependsOn(JwtMessageHelper.BEAN_NAME)
class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint, Serializable {

    static final String BEAN_NAME = "edu.awieclawski.app.jwt.inn.JwtAuthenticationEntryPoint";

    private static final long serialVersionUID = -7858869558953243875L;

    private final JwtMessageHelper messageHelper;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {
        HttpStatus httpStatus = getHttpStatus(authException);
        response.setStatus(getHttpResponse(authException));
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        Exception exception = (Exception) request.getAttribute(JwtRequestFilter.REQUEST_EXCEPTION_ATTRIBUTE);
        String message;

        if (exception != null) {
            message = exception.getMessage();
        } else {
            if (authException.getCause() != null) {
                message = authException.getCause().getMessage() + " " + authException.getMessage();
            } else {
                message = authException.getMessage();
            }
        }

        messageHelper.handleMessage(httpStatus, message, request, response, authException,null);
    }

    private HttpStatus getHttpStatus(Exception exception) {
        log.debug("getHttpStatus ref. Exception: {}", exception.getClass().getSimpleName());
        return HttpStatus.UNAUTHORIZED;
    }

    private Integer getHttpResponse(Exception exception) {
        log.debug("getHttpResponse ref. Exception: {}", exception.getClass().getSimpleName());
        return HttpServletResponse.SC_UNAUTHORIZED;
    }
}
