package com.example.daemawiki.domain.user.api;

import com.example.daemawiki.domain.user.dto.ChangePasswordRequest;
import com.example.daemawiki.domain.user.service.ChangePassword;
import com.example.daemawiki.domain.user.service.ProfileImageUpload;
import org.springframework.http.HttpStatus;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

/*

    비밀번호 변경
    프로필 이미지 변경
    ...
*/
@RestController
@RequestMapping("/api/users")
public class UserController {
    private final ProfileImageUpload profileImageUpload;
    private final ChangePassword changePasswordService;

    public UserController(ProfileImageUpload profileImageUpload, ChangePassword changePasswordService) {
        this.profileImageUpload = profileImageUpload;
        this.changePasswordService = changePasswordService;
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

}
