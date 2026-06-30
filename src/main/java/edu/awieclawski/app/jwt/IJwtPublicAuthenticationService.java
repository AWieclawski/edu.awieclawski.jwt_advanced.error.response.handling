package edu.awieclawski.app.jwt;

import edu.awieclawski.app.dto.UserResponseDTO;
import edu.awieclawski.app.jwt.dto.JwtUserDTO;
import edu.awieclawski.app.jwt.dto.JwtRequest;
import edu.awieclawski.app.jwt.dto.JwtResponse;
import org.springframework.security.core.Authentication;

public interface IJwtPublicAuthenticationService {

    Authentication getAuthentication();

    JwtResponse createAuthenticationToken(JwtRequest jwtRequest) throws Exception;

    JwtResponse refreshToken();

    UserResponseDTO saveUser(JwtUserDTO jwtUserDTO);

    UserResponseDTO updateUser(JwtUserDTO user);
}
