package org.daemawiki.domain.admin.adapter;

import org.daemawiki.domain.admin.application.CreateAdminAccountPort;
import org.daemawiki.domain.admin.application.DeleteAdminAccountPort;
import org.daemawiki.domain.admin.application.FindAdminAccountPort;
import org.daemawiki.domain.admin.model.Admin;
import org.daemawiki.domain.admin.repository.AdminAccountRepository;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class AdminAccountAdapter implements FindAdminAccountPort, CreateAdminAccountPort, DeleteAdminAccountPort {
    private final AdminAccountRepository adminAccountRepository;

    public AdminAccountAdapter(AdminAccountRepository adminAccountRepository) {
        this.adminAccountRepository = adminAccountRepository;
    }

    @Override
    public Mono<Admin> create(Admin admin) {
        return adminAccountRepository.save(admin);
    }

    @Override
    public Mono<Void> deleteByUserId(String userId) {
        return adminAccountRepository.deleteByUserId(userId);
    }

    @Override
    public Mono<Void> deleteByEmail(String email) {
        return adminAccountRepository.deleteByEmail(email);
    }

    @Override
    public Mono<Admin> findByUserId(String userId) {
        return adminAccountRepository.findByUserId(userId);
    }

    @Override
    public Mono<Admin> findByEmail(String email) {
        return adminAccountRepository.findByEmail(email);
    }

    @Override
    public Flux<Admin> findAll() {
        return adminAccountRepository.findAll();
    }

    @Override
    public Mono<Boolean> existsByEmail(String email) {
        return adminAccountRepository.existsByEmail(email);
    }

}
