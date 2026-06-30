package edu.awieclawski.app.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ResourceController {

    @RequestMapping({"/", "${jwt.api.open.hello}"})
    public String firstPage() {
        return "Hello open world";
    }

}
