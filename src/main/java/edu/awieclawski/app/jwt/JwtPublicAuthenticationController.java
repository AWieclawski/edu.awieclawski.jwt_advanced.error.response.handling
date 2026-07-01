package edu.awieclawski.app.jwt;

import edu.awieclawski.app.jwt.dto.JwtRequest;
import edu.awieclawski.app.jwt.dto.JwtUserDTO;
import edu.awieclawski.app.jwt.inn.JwtPublicAuthenticationService;
import edu.awieclawski.app.model.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.DependsOn;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;

@RequiredArgsConstructor
@RestController
@RequestMapping(value = JwtPublicAuthenticationController.API_PATH)
@CrossOrigin
@DependsOn({JwtPublicAuthenticationService.BEAN_NAME})
public class JwtPublicAuthenticationController {

    public static final String API_PATH = "/api/v1";

    private final IJwtPublicAuthenticationService jwtPublicAuthenticationService;

    @RequestMapping(value = "${jwt.api.open.login}", method = RequestMethod.POST)
    public ResponseEntity<Object> createAuthenticationToken(@RequestBody JwtRequest authenticationRequest) throws Exception {
        return ResponseEntity.ok()
                .body(jwtPublicAuthenticationService
                        .createAuthenticationToken(authenticationRequest));
    }

    @RequestMapping(value = "${jwt.api.open.register}", method = RequestMethod.POST)
    public ResponseEntity<Object> createBasicUser(@RequestBody JwtUserDTO jwtUserDTO) {
        // user role set by not secured end point must be default
        jwtUserDTO.setRoles(Collections.singletonList(UserRole.BASIC.getRoleId()));
        return ResponseEntity.ok()
                .body(jwtPublicAuthenticationService
                        .saveUser(jwtUserDTO));
    }

}
