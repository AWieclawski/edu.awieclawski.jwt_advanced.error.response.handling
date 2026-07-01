package edu.awieclawski.app.jwt.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.List;

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
    @Setter
    List<Integer> roles;
}
