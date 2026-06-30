package edu.awieclawski.app.jwt;

import edu.awieclawski.app.exception.RequestHeaderValueException;
import edu.awieclawski.app.jwt.dto.JwtUserDTO;
import edu.awieclawski.app.jwt.inn.JwtPublicAuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.DependsOn;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping(value = JwtSecuredAuthenticationController.API_PATH)
@CrossOrigin
@DependsOn({JwtPublicAuthenticationService.BEAN_NAME})
public class JwtSecuredAuthenticationController {

    public static final String API_PATH = "/secured/api/v1";

    private final IJwtPublicAuthenticationService jwtPublicAuthenticationService;

    @RequestMapping(value = "${jwt.api.safe.refresh-token}", method = RequestMethod.POST)
    public ResponseEntity<Object> refreshToken(@RequestHeader("refreshToken") String isRefreshToken) {
        if (isRefreshToken.equals("true")) {
            return ResponseEntity.ok()
                    .body(jwtPublicAuthenticationService.refreshToken());
        }
        throw new RequestHeaderValueException("Request Header not true");
    }


    @RequestMapping(value = "${jwt.api.safe.user-update}", method = RequestMethod.POST)
    public ResponseEntity<Object> updateUser(@RequestBody JwtUserDTO user) {
        return ResponseEntity.ok()
                .body(jwtPublicAuthenticationService.updateUser(user));
    }

}
