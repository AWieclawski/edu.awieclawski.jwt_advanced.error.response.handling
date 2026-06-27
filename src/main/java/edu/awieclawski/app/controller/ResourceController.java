package edu.awieclawski.app.controller;

import edu.awieclawski.app.dto.ErrorResponseDto;
import edu.awieclawski.app.dto.UserResponseDTO;
import edu.awieclawski.app.facade.ResourceFacade;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ResourceController {

    private final ResourceFacade resourceFacade;

    @RequestMapping({"/", "${jwt.api.open.hello}"})
    public String firstPage() {
        return "Hello open world";
    }

    @RequestMapping("${jwt.api.safe.user}")
    public ResponseEntity<?> getUser() {
        UserResponseDTO jwtUserDTO = resourceFacade.getAuthenticatedUser();
        return ResponseEntity.ok("Hello authorised User: " + jwtUserDTO.getLogin());
    }

    @RequestMapping("${jwt.api.safe.admin}")
    public ResponseEntity<?> getAdmin() {
        UserResponseDTO jwtUserDTO = resourceFacade.getAuthenticatedUser();
        return ResponseEntity.ok("Hello authorised Admin: " + jwtUserDTO.getLogin());
    }

    @RequestMapping("${jwt.api.safe.errors}")
    public ResponseEntity<?> getAdminErrors() {
        List<ErrorResponseDto> allErrorMessages = resourceFacade.getErrors();
        return ResponseEntity.ok().body(allErrorMessages);
    }

}
