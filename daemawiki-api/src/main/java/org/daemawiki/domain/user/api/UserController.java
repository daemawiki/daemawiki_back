package org.daemawiki.domain.user.api;

import jakarta.annotation.Nullable;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.daemawiki.domain.user.dto.request.ChangePasswordRequest;
import org.daemawiki.domain.user.dto.request.UpdateClubRequest;
import org.daemawiki.domain.user.dto.response.GetUserResponse;
import org.daemawiki.domain.user.usecase.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final ChangeUserPasswordUsecase changeUserPasswordUsecase;
    private final DeleteUserUsecase deleteUserUsecase;
    private final GetUserUsecase getUserUsecase;
    private final UpdateUserUsecase updateUserUsecase;
    private final UploadUserProfileImageUsecase uploadUserProfileImageUsecase;

    public UserController(ChangeUserPasswordUsecase changeUserPasswordUsecase, DeleteUserUsecase deleteUserUsecase, GetUserUsecase getUserUsecase, UpdateUserUsecase updateUserUsecase, UploadUserProfileImageUsecase uploadUserProfileImageUsecase) {
        this.changeUserPasswordUsecase = changeUserPasswordUsecase;
        this.deleteUserUsecase = deleteUserUsecase;
        this.getUserUsecase = getUserUsecase;
        this.updateUserUsecase = updateUserUsecase;
        this.uploadUserProfileImageUsecase = uploadUserProfileImageUsecase;
    }

    @PatchMapping(value = "/profile-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> profileUpload(@RequestPart(value = "file", required = true) FilePart filePart) {
        return uploadUserProfileImageUsecase.upload(filePart);
    }

    @PatchMapping("/password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> changePasswordCurrentUser(@Valid @RequestBody ChangePasswordRequest request) {
        return changeUserPasswordUsecase.currentUser(request);
    }
    
    @PatchMapping("/password/non-login")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> changePasswordNonLoggedInUser (@Valid @RequestBody ChangePasswordRequest request) {
        return changeUserPasswordUsecase.nonLoggedInUser(request);
    }

    @GetMapping("/generation/{generation}")
    public Flux<GetUserResponse> getUserByGen(@NotNull @PathVariable Integer generation) {
        return getUserUsecase.getUserByGen(generation);
    }

    @GetMapping("/major/{major}")
    public Flux<GetUserResponse> getUserByMajor(@NotBlank @PathVariable String major) {
        return getUserUsecase.getUserByMajor(major);
    }

    @GetMapping("/club/{club}")
    public Flux<GetUserResponse> getUserByClub(@NotNull @PathVariable String club) {
        return getUserUsecase.getUserByClub(club);
    }

    @GetMapping("/find")
    public Flux<GetUserResponse> getUser(@Nullable @RequestParam Integer gen, @Nullable @RequestParam String major, @Nullable @RequestParam String club, @RequestParam String orderBy, @Nullable @RequestParam String sort) {
        return getUserUsecase.getUserByGenAndMajorAndClub(gen, major, club, orderBy, sort);
    }

    @GetMapping
    public Mono<GetUserResponse> getCurrentUser() {
        return getUserUsecase.getCurrentUser();
    }

    @PatchMapping("/my/club")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> updateClub(@Valid @RequestBody UpdateClubRequest request) {
        return updateUserUsecase.updateUserClub(request);
    }

    @DeleteMapping
    public Mono<Void> deleteUser() {
        return deleteUserUsecase.deleteCurrentUser();
    }

}
