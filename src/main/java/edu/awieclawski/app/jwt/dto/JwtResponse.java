package edu.awieclawski.app.jwt.dto;

import lombok.*;

import java.io.Serializable;

@Builder
@Getter
@AllArgsConstructor
@RequiredArgsConstructor
public class JwtResponse implements Serializable {

    private static final long serialVersionUID = -4321468587395150780L;

    private String jwtToken;
    private String tokenValidTo;

}
