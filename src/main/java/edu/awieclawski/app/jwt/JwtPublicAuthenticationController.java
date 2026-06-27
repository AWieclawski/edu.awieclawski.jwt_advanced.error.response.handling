package edu.awieclawski.app.jwt;

import edu.awieclawski.app.jwt.dto.JwtUserDTO;
import edu.awieclawski.app.exception.RequestHeaderValueException;
import edu.awieclawski.app.jwt.dto.JwtRequest;
import edu.awieclawski.app.jwt.inn.JwtPublicAuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.DependsOn;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@CrossOrigin
@DependsOn({JwtPublicAuthenticationService.BEAN_NAME})
public class JwtPublicAuthenticationController {

    private final IJwtPublicAuthenticationService jwtPublicAuthenticationService;

    @RequestMapping(value = "${jwt.api.open.login}", method = RequestMethod.POST)
    public ResponseEntity<Object> createAuthenticationToken(@RequestBody JwtRequest authenticationRequest) throws Exception {
        return ResponseEntity.ok()
                .body(jwtPublicAuthenticationService
                        .createAuthenticationToken(authenticationRequest));
    }

    @RequestMapping(value = "${jwt.api.open.register}", method = RequestMethod.POST)
    public ResponseEntity<Object> saveUser(@RequestBody JwtUserDTO user) {
        return ResponseEntity.ok()
                .body(jwtPublicAuthenticationService
                        .saveUser(user));
    }

    @RequestMapping(value = "${jwt.api.safe.refresh-token}", method = RequestMethod.POST)
    public ResponseEntity<Object> refreshToken(@RequestHeader("refreshToken") String isRefreshToken) {
        Authentication authentication = jwtPublicAuthenticationService.getAuthentication();
        if (isRefreshToken.equals("true")) {
            return ResponseEntity.ok()
                    .body(jwtPublicAuthenticationService.refreshToken(authentication));
        }
        throw new RequestHeaderValueException("Request Header not true");
    }

}
