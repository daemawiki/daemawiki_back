package com.example.daemawiki.domain.auth.api;

import com.example.daemawiki.domain.auth.dto.LoginRequest;
import com.example.daemawiki.domain.auth.dto.ReissueRequest;
import com.example.daemawiki.domain.auth.dto.TokenResponse;
import com.example.daemawiki.domain.auth.dto.SignupRequest;
import com.example.daemawiki.domain.auth.service.Login;
import com.example.daemawiki.domain.auth.service.Signup;
import com.example.daemawiki.global.security.Tokenizer;
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
    private final Tokenizer tokenizer;

    public AuthController(Login loginService, Signup signupService, Tokenizer tokenizer) {
        this.loginService = loginService;
        this.signupService = signupService;
        this.tokenizer = tokenizer;
    }

    @PostMapping("/login")
    public Mono<TokenResponse> login(@RequestBody LoginRequest request) {
        return loginService.execute(request);
    }

    @PostMapping("/signup")
    public Mono<Void> signup(@RequestBody SignupRequest request) {
        return signupService.execute(request);
    }

    @PostMapping("/reissue")
    public Mono<TokenResponse> reissue(@RequestBody ReissueRequest request) {
        return tokenizer.reissue(request);
    }

}
