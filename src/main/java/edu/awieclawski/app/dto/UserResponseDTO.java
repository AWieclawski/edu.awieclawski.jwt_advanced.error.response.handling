package edu.awieclawski.app.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserResponseDTO {
    private String login;
    private String name;
    private String email;
    private Integer roleId;
}
