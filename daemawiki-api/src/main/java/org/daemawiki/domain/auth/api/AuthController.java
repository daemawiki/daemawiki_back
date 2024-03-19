package org.daemawiki.domain.auth.api;

import jakarta.validation.Valid;
import org.daemawiki.domain.auth.dto.request.LoginRequest;
import org.daemawiki.domain.auth.dto.request.ReissueRequest;
import org.daemawiki.domain.auth.dto.request.SignupRequest;
import org.daemawiki.domain.auth.dto.response.TokenResponse;
import org.daemawiki.domain.auth.usecase.ReissueUsecase;
import org.daemawiki.domain.auth.usecase.SigninUsecase;
import org.daemawiki.domain.auth.usecase.SignupUsecase;
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

    @PostMapping("/signup")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Void> signup(@Valid @RequestBody SignupRequest request) {
        return signupUsecase.signup(request);
    }

    @PostMapping("/reissue")
    public Mono<TokenResponse> reissue(@Valid @RequestBody ReissueRequest request) {
        return reissueUsecase.reissue(request);
    }

}
