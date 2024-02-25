package com.example.daemawiki.domain.user.api;

import com.example.daemawiki.domain.user.dto.ChangePasswordRequest;
import com.example.daemawiki.domain.user.dto.GetUserResponse;
import com.example.daemawiki.domain.user.service.ChangePassword;
import com.example.daemawiki.domain.user.service.GetUser;
import com.example.daemawiki.domain.user.service.ProfileImageUpload;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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

    @PatchMapping(value = "/profile-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> profileUpload(@RequestPart(value = "file", required = true) FilePart filePart) {
        return profileImageUpload.execute(filePart);
    }

    @PatchMapping("/password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> changePasswordCurrentUser(@Valid @RequestBody ChangePasswordRequest request) {
        return changePasswordService.currentUser(request);
    }
    
    @PatchMapping("/non-login/password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> changePasswordNonLoggedInUser (@Valid @RequestBody ChangePasswordRequest request) {
        return changePasswordService.nonLoggedInUser(request);
    }

    @GetMapping("/gen")
    public Flux<GetUserResponse> getUserByGen(@NotNull @RequestParam Integer param) {
        return getUser.getUserByGen(param);
    }

    @GetMapping("/major")
    public Flux<GetUserResponse> getUserByMajor(@NotBlank @RequestParam String param) {
        return getUser.getUserByMajor(param);
    }

    @GetMapping("/club")
    public Flux<GetUserResponse> getUserByClub(@NotNull @RequestParam String param) {
        return getUser.getUserByClub(param);
    }

    @GetMapping
    public Mono<GetUserResponse> getCurrentUser() {
        return getUser.getCurrentUser();
    }

}
