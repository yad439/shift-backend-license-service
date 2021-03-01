package ru.cft.licenseservice.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.cft.licenseservice.service.SimpleService;

@RestController
@RequestMapping("/")
public class SimpleController {

    private final SimpleService simpleService;

    public SimpleController(SimpleService simpleService) {
        this.simpleService = simpleService;
    }

    @GetMapping("create")
    public String create() {
        return simpleService.createLicense().toString();
    }
}
