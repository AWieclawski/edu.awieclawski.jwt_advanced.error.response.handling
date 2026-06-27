package edu.awieclawski.app.mapper;

import edu.awieclawski.app.dto.ErrorResponseDto;
import edu.awieclawski.app.exception.ErrorIdSolver;
import edu.awieclawski.app.model.ErrorMessageEntity;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import javax.servlet.http.HttpServletRequest;
import java.sql.Timestamp;
import java.util.UUID;

import static edu.awieclawski.app.util.DateTimeUtils.stringToTimestampMulti;
import static edu.awieclawski.app.util.DateTimeUtils.timestampToStringMulti;


@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ErrorMessageMapper {

    public static ErrorResponseDto toDto(ErrorMessageEntity entity) {
        return ErrorResponseDto.builder()
                .errorId(entity.getErrorId())
                .message(entity.getMessage())
                .url(entity.getUrl())
                .errorCode(entity.getErrorCode())
                .userLogin(entity.getUserLogin())
                .tokenShort(entity.getTokenShort())
                .createdAt(timestampToStringMulti(entity.getCreatedAt()))
                .build();
    }

    public static ErrorResponseDto getErrorResponseDto(HttpStatus httpStatus, String message,
                                                       Exception exception, Timestamp createdAt) {
        UUID uuid = ErrorIdSolver.solve(exception);
        return ErrorResponseDto.builder()
                .errorId(uuid)
                .message(message)
                .errorCode(httpStatus.value())
                .createdAt(timestampToStringMulti(createdAt))
                .build();
    }

    public static ErrorResponseDto getErrorResponseDto(HttpStatus httpStatus, String message,
                                                       HttpServletRequest request, Exception exception,
                                                       Timestamp createdAt, String userLogin) {
        UUID uuid = ErrorIdSolver.solve(exception);
        return ErrorResponseDto.builder()
                .errorId(userLogin != null ? uuid : null)
                .message(message)
                .errorCode(httpStatus.value())
                .userLogin(userLogin)
                .url(request.getRequestURI())
                .createdAt(timestampToStringMulti(createdAt))
                .build();
    }

    public static ErrorMessageEntity toEntity(ErrorResponseDto errorResponseDto) {
        return ErrorMessageEntity.builder()
                .errorId(errorResponseDto.getErrorId())
                .message(errorResponseDto.getMessage())
                .url(errorResponseDto.getUrl())
                .errorCode(errorResponseDto.getErrorCode())
                .userLogin(errorResponseDto.getUserLogin())
                .tokenShort(errorResponseDto.getTokenShort())
                .createdAt(stringToTimestampMulti(errorResponseDto.getCreatedAt()))
                .build();
    }


}
