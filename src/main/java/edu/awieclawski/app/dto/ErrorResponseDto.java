package edu.awieclawski.app.dto;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponseDto {

    Integer errorCode;
    String message;
    String url;
    UUID errorId;
    String userLogin;
    String tokenShort;
    String createdAt;

    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + (errorCode != null ? errorCode.hashCode() : 0);
        result = 31 * result + (message != null ? message.hashCode() : 0);
        result = 31 * result + (url != null ? url.hashCode() : 0);
        result = 31 * result + (tokenShort != null ? tokenShort.hashCode() : 0);
        return result;
    }
}
