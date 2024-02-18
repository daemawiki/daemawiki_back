package com.example.daemawiki.domain.user.api;

import com.example.daemawiki.domain.user.dto.ChangePasswordRequest;
import com.example.daemawiki.domain.user.dto.GetUserResponse;
import com.example.daemawiki.domain.user.service.ChangePassword;
import com.example.daemawiki.domain.user.service.GetUser;
import com.example.daemawiki.domain.user.service.ProfileImageUpload;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final ProfileImageUpload profileImageUpload;
    private final ChangePassword changePasswordService;
    private final GetUser getUser;

    public UserController(ProfileImageUpload profileImageUpload, ChangePassword changePasswordService, GetUser getUser) {
        this.profileImageUpload = profileImageUpload;
        this.changePasswordService = changePasswordService;
        this.getUser = getUser;
    }

    @PatchMapping("/profile-image")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> profileUpload(@RequestPart FilePart filePart) {
        return profileImageUpload.execute(filePart);
    }

    @PatchMapping("/password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> changePassword(@RequestBody ChangePasswordRequest request) {
        return changePasswordService.execute(request);
    }

    @GetMapping
    public Flux<GetUserResponse> getUserByGen(@NotNull @RequestParam Integer gen) {
        return getUser.getUserByGen(gen);
    }

    @GetMapping
    public Flux<GetUserResponse> getUserByMajor(@NotBlank @RequestParam String major) {
        return getUser.getUserByMajor(major);
    }

}
