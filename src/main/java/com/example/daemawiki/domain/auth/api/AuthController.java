package com.example.daemawiki.domain.auth.api;

import com.example.daemawiki.domain.auth.dto.LoginRequest;
import com.example.daemawiki.domain.auth.dto.LoginResponse;
import com.example.daemawiki.domain.auth.dto.SignupRequest;
import com.example.daemawiki.domain.auth.service.Login;
import com.example.daemawiki.domain.auth.service.Signup;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final Login loginService;
    private final Signup signupService;

    public AuthController(Login loginService, Signup signupService) {
        this.loginService = loginService;
        this.signupService = signupService;
    }

    @PostMapping("/login")
    public Mono<LoginResponse> login(@RequestBody LoginRequest request) {
        return loginService.execute(request);
    }

    @PostMapping("/signup")
    public Mono<Void> signup(@RequestBody SignupRequest request) {
        return signupService.execute(request);
    }

}
