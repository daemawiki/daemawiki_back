package org.daemawiki.domain.user.usecase;

import org.daemawiki.domain.user.dto.request.ChangePasswordRequest;
import reactor.core.publisher.Mono;

public interface ChangeUserPasswordUsecase {
    Mono<Void> currentUser(ChangePasswordRequest request);
    Mono<Void> nonLoggedInUser(ChangePasswordRequest request);

}
