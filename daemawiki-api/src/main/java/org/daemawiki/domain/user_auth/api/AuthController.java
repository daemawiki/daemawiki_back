package org.daemawiki.domain.user_auth.api;

import jakarta.validation.Valid;
import org.daemawiki.domain.user_auth.dto.request.LoginRequest;
import org.daemawiki.domain.user_auth.dto.request.SignupRequest;
import org.daemawiki.domain.user_auth.dto.response.TokenResponse;
import org.daemawiki.domain.user_auth.usecase.ReissueUsecase;
import org.daemawiki.domain.user_auth.usecase.SigninUsecase;
import org.daemawiki.domain.user_auth.usecase.SignupUsecase;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final SigninUsecase signinUsecase;
    private final SignupUsecase signupUsecase;
    private final ReissueUsecase reissueUsecase;

    public AuthController(SigninUsecase signinUsecase, SignupUsecase signupUsecase, ReissueUsecase reissueUsecase) {
        this.signinUsecase = signinUsecase;
        this.signupUsecase = signupUsecase;
        this.reissueUsecase = reissueUsecase;
    }

    @PostMapping("/login")
    public Mono<TokenResponse> login(@Valid @RequestBody LoginRequest request) {
        return signinUsecase.signin(request);
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Void> signup(@Valid @RequestBody SignupRequest request) {
        return signupUsecase.signup(request);
    }

    @PutMapping("/reissue")
    public Mono<TokenResponse> reissue(@RequestHeader("Authorization") String token) {
        return reissueUsecase.reissue(token);
    }

}
