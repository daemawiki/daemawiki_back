package org.daemawiki.domain.admin.service;

import org.daemawiki.domain.admin.application.FindAdminAccountPort;
import org.daemawiki.domain.admin.dto.GetAdminResponse;
import org.daemawiki.domain.admin.model.Admin;
import org.daemawiki.domain.admin.usecase.GetAdminUsecase;
import org.daemawiki.domain.user.application.FindUserPort;
import org.daemawiki.exception.h403.NoPermissionUserException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class GetAdminService implements GetAdminUsecase {
    private final FindAdminAccountPort findAdminAccountPort;
    private final FindUserPort findUserPort;

    public GetAdminService(FindAdminAccountPort findAdminAccountPort, FindUserPort findUserPort) {
        this.findAdminAccountPort = findAdminAccountPort;
        this.findUserPort = findUserPort;
    }

    @Override
    public Flux<GetAdminResponse> getAll() {
        return validate()
                .flatMapMany(admin -> findAdminAccountPort.findAll()
                        .map(GetAdminResponse::of));
    }

    private Mono<Admin> validate() {
        return findUserPort.currentUser()
                .flatMap(user -> findAdminAccountPort.findByUserId(user.getId()))
                .switchIfEmpty(Mono.error(NoPermissionUserException.EXCEPTION));
    }

}
