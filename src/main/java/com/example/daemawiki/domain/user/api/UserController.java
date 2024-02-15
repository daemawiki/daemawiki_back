package com.example.daemawiki.domain.user.api;

import com.example.daemawiki.domain.user.service.ProfileImageUpload;
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

    public UserController(ProfileImageUpload profileImageUpload) {
        this.profileImageUpload = profileImageUpload;
    }

    @PatchMapping("/profile-image")
    public Mono<Void> profileUpload(@RequestPart FilePart filePart) {
        return profileImageUpload.execute(filePart);
    }

}
