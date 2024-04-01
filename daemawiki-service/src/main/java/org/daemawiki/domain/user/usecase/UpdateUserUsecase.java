package org.daemawiki.domain.user.usecase;

import org.daemawiki.domain.user.dto.request.EditUserRequest;
import org.daemawiki.domain.user.dto.request.UpdateClubRequest;
import reactor.core.publisher.Mono;

public interface UpdateUserUsecase {
    Mono<Void> updateUser(EditUserRequest request);
    Mono<Void> updateUserClub(UpdateClubRequest request);

}
