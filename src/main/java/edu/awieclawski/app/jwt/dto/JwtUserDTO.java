package edu.awieclawski.app.jwt.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@ToString(exclude = {"password"})
@JsonInclude(JsonInclude.Include.NON_NULL)
public class JwtUserDTO {
    private String login;
    private String name;
    @Setter
    private String password;
    private String email;
    private String role;
}
