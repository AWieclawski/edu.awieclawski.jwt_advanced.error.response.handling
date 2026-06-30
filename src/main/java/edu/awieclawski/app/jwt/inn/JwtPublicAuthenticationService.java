package edu.awieclawski.app.jwt.inn;

import edu.awieclawski.app.dto.UserResponseDTO;
import edu.awieclawski.app.exception.UpdateEntityException;
import edu.awieclawski.app.jwt.IJwtPublicAuthenticationService;
import edu.awieclawski.app.jwt.dto.JwtRequest;
import edu.awieclawski.app.jwt.dto.JwtResponse;
import edu.awieclawski.app.jwt.dto.JwtUserDTO;
import edu.awieclawski.app.jwt.exception.JwtAuthenticationException;
import edu.awieclawski.app.model.UserRole;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.DependsOn;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 *
 * https://www.geeksforgeeks.org/web-tech/json-web-token-jwt/
 * <p>
 * https://www.baeldung.com/get-user-in-spring-security
 * <p>
 * https://stackoverflow.com/questions/32052076/how-to-get-the-current-logged-in-user-object-from-spring-security
 * <p>
 * https://www.javathinking.com/blog/retrieve-user-information-in-spring-security/
 *
 */

@Slf4j
@Service(JwtPublicAuthenticationService.BEAN_NAME)
@RequiredArgsConstructor
@DependsOn({JwtUserDetailsService.BEAN_NAME, JwtTokenUtil.BEAN_NAME})
public class JwtPublicAuthenticationService implements IJwtPublicAuthenticationService {

    public static final String BEAN_NAME = "edu.awieclawski.app.jwt.inn.JwtPublicAuthenticationService";

    private final AuthenticationManager authenticationManager;
    private final JwtTokenUtil jwtTokenUtil;
    private final JwtUserDetailsService userDetailsService;

    public Authentication getAuthentication() {
        return Optional.ofNullable(
                SecurityContextHolder.getContext().getAuthentication()
        ).orElseThrow(() -> new JwtAuthenticationException("Getting User from Security Context error!"));
    }

    public JwtResponse createAuthenticationToken(JwtRequest jwtRequest) throws Exception {
        authenticate(jwtRequest.getLogin(), jwtRequest.getPassword());
        final UserDetails userDetails = userDetailsService.loadUserByUsername(jwtRequest.getLogin());
        JwtResponse jwtResponse = jwtTokenUtil.generateToken(userDetails, false);
        log.info("New Jwt token [{}] created for: [{}]", jwtTokenUtil.getTokenShort(jwtResponse.getJwtToken()), userDetails.getUsername());
        return jwtResponse;
    }

    public JwtResponse refreshToken() {
        Authentication authentication = getAuthentication();
        final UserDetails userDetails = userDetailsService.loadUserByUsername(authentication.getName());
        JwtResponse jwtResponse = jwtTokenUtil.generateToken(userDetails, true);
        log.info("Jwt token [{}] refreshed for: [{}]", jwtTokenUtil.getTokenShort(jwtResponse.getJwtToken()), userDetails.getUsername());
        return jwtResponse;
    }

    private void authenticate(String username, String password) throws Exception {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        } catch (DisabledException e) {
            throw new Exception("Account verification failed", e);
        } catch (BadCredentialsException e) {
            throw new Exception("Invalid credentials", e);
        }
    }

    @Override
    public UserResponseDTO updateUser(JwtUserDTO user) {
        Authentication authentication = getAuthentication();
        final UserDetails authenticatedUser = userDetailsService.loadUserByUsername(authentication.getName());
        boolean hasAdminRole = authenticatedUser.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals(UserRole.SUPER.getRole()));
        if (hasAdminRole) {
            return userDetailsService.updateEncodedUser(user);
        }
        throw new UpdateEntityException("Authorised User " + authenticatedUser.getUsername() + " is not allowed to update. " + user.getLogin());
    }

    public UserResponseDTO saveUser(JwtUserDTO jwtUserDTO) {
        return userDetailsService.saveEncodedUser(jwtUserDTO);
    }
}
