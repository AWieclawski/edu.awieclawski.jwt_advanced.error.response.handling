package edu.awieclawski.app.jwt.inn;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.awieclawski.app.config.AppDateTimeProvider;
import edu.awieclawski.app.mapper.ErrorMessageMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.DependsOn;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.sql.Timestamp;

@Slf4j
@Component(value = JwtMessageHelper.BEAN_NAME)
@RequiredArgsConstructor
@DependsOn(AppDateTimeProvider.BEAN_NAME)
class JwtMessageHelper implements Serializable {

    static final String BEAN_NAME = "edu.awieclawski.app.jwt.inn.JwtMessageHelper";

    private static final long serialVersionUID = 3798695510432438656L;

    private final AppDateTimeProvider appDateTimeProvider;

    private final ObjectMapper objectMapper = new ObjectMapper();

    void handleMessage(HttpStatus httpStatus,
                       String message,
                       HttpServletRequest request,
                       HttpServletResponse response,
                       Exception exception, String userLogin) throws IOException, ServletException {
        Timestamp createdAt = appDateTimeProvider.getCurrentTimestamp();
        message = objectMapper.writeValueAsString(ErrorMessageMapper.getErrorResponseDto(httpStatus, message, request,
                exception, createdAt, userLogin));
        Writer out;
        if (response.getWriter() != null) {
            out = response.getWriter();
        } else {
            out = new BufferedWriter(new OutputStreamWriter(response.getOutputStream()));
        }
        out.write(message);
    }
}
