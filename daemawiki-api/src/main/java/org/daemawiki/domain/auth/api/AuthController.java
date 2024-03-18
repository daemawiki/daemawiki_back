package org.daemawiki.domain.auth.api;

import org.daemawiki.domain.auth.dto.request.LoginRequest;
import org.daemawiki.domain.auth.dto.request.ReissueRequest;
import org.daemawiki.domain.auth.dto.response.TokenResponse;
import org.daemawiki.domain.auth.dto.request.SignupRequest;
import org.daemawiki.domain.auth.service.Login;
import org.daemawiki.domain.auth.service.Reissue;
import org.daemawiki.domain.auth.service.Signup;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final Login loginService;
    private final Signup signupService;
    private final Reissue reissue;

    public AuthController(Login loginService, Signup signupService, Reissue reissue) {
        this.loginService = loginService;
        this.signupService = signupService;
        this.reissue = reissue;
    }

    @PostMapping("/login")
    public Mono<TokenResponse> login(@Valid @RequestBody LoginRequest request) {
        return loginService.execute(request);
    }

    @PostMapping("/signup")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Void> signup(@Valid @RequestBody SignupRequest request) {
        return signupService.execute(request);
    }

    @PostMapping("/reissue")
    public Mono<TokenResponse> reissue(@Valid @RequestBody ReissueRequest request) {
        return reissue.execute(request);
    }

}
