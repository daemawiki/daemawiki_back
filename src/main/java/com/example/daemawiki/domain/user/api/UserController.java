package com.example.daemawiki.domain.user.api;

import com.example.daemawiki.domain.user.dto.request.ChangePasswordRequest;
import com.example.daemawiki.domain.user.dto.response.GetUserResponse;
import com.example.daemawiki.domain.user.dto.request.UpdateClubRequest;
import com.example.daemawiki.domain.user.service.ChangePassword;
import com.example.daemawiki.domain.user.service.GetUser;
import com.example.daemawiki.domain.user.service.ProfileImageUpload;
import com.example.daemawiki.domain.user.service.UpdateClub;
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
    private final UpdateClub updateClub;

    public UserController(ProfileImageUpload profileImageUpload, ChangePassword changePasswordService, GetUser getUser, UpdateClub updateClub) {
        this.profileImageUpload = profileImageUpload;
        this.changePasswordService = changePasswordService;
        this.getUser = getUser;
        this.updateClub = updateClub;
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

    @GetMapping("/generation/{generation}")
    public Flux<GetUserResponse> getUserByGen(@NotNull @PathVariable Integer generation) {
        return getUser.getUserByGen(generation);
    }

    @GetMapping("/major/{major}")
    public Flux<GetUserResponse> getUserByMajor(@NotBlank @PathVariable String major) {
        return getUser.getUserByMajor(major);
    }

    @GetMapping("/club/{club}")
    public Flux<GetUserResponse> getUserByClub(@NotNull @PathVariable String club) {
        return getUser.getUserByClub(club);
    }

    @GetMapping("/find")
    public Flux<GetUserResponse> getUser(@RequestParam Integer gen, @RequestParam String major, @RequestParam String club, @RequestParam String orderBy, @RequestParam String sort) {
        return getUser.getUserByGenAndMajorAndClub(gen, major, club, orderBy, sort);
    }

    @GetMapping
    public Mono<GetUserResponse> getCurrentUser() {
        return getUser.getCurrentUser();
    }

    @PatchMapping("/my/club")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> updateClub(@Valid @RequestBody UpdateClubRequest request) {
        return updateClub.execute(request);
    }

}
