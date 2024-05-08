package org.daemawiki.domain.admin.service;

import org.daemawiki.domain.admin.application.FindAdminAccountPort;
import org.daemawiki.domain.admin.component.ValidateAdminComponent;
import org.daemawiki.domain.admin.dto.GetAdminResponse;
import org.daemawiki.domain.admin.usecase.GetAdminUsecase;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
public class GetAdminService implements GetAdminUsecase {
    private final FindAdminAccountPort findAdminAccountPort;
    private final ValidateAdminComponent validateAdminComponent;

    public GetAdminService(FindAdminAccountPort findAdminAccountPort, ValidateAdminComponent validateAdminComponent) {
        this.findAdminAccountPort = findAdminAccountPort;
        this.validateAdminComponent = validateAdminComponent;
    }

    @Override
    public Flux<GetAdminResponse> getAll() {
        return validateAdminComponent.validateAdmin()
                .flatMapMany(admin -> findAdminAccountPort.findAll()
                        .map(GetAdminResponse::of));
    }

}
