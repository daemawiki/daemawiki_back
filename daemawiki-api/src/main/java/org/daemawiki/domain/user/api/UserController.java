package org.daemawiki.domain.user.api;

import jakarta.validation.Valid;
import org.daemawiki.domain.user.dto.request.ChangePasswordRequest;
import org.daemawiki.domain.user.dto.request.UpdateUserRequest;
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

    @GetMapping("/find")
    public Flux<GetUserResponse> getUser(
            @RequestParam(required = false) Integer gen,
            @RequestParam(required = false) String major,
            @RequestParam(required = false) String club,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "1" ) Integer sortDirection,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size
    ) {
        return getUserUsecase.getUserByGenAndMajorAndClub(
                gen, major, club, sortBy, sortDirection, page, size
        );
    }

    @GetMapping
    public Mono<GetUserResponse> getCurrentUser() {
        return getUserUsecase.getCurrentUser();
    }

    @PatchMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> updateUser(@Valid @RequestBody UpdateUserRequest request) {
        return updateUserUsecase.updateUser(request);
    }

    @DeleteMapping
    public Mono<Void> deleteUser() {
        return deleteUserUsecase.deleteCurrentUser();
    }

}
