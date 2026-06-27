package edu.awieclawski.app.jwt.dto;

import lombok.*;

import java.io.Serializable;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class JwtRequest implements Serializable {

    private static final long serialVersionUID = 5432468583005156531L;

    private String username;
    private String password;
}
