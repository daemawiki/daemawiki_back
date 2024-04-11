package org.daemawiki.domain.admin.api;

import org.daemawiki.domain.admin.dto.GetAdminResponse;
import org.daemawiki.domain.admin.usecase.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    private final GetAdminUsecase getAdminUsecase;
    private final AddAdminUsecase addAdminUsecase;
    private final RemoveAdminUsecase removeAdminUsecase;
    private final BlockUserUsecase blockUserUsecase;
    private final UnBlockUserUsecase unBlockUserUsecase;

    public AdminController(GetAdminUsecase getAdminUsecase, AddAdminUsecase addAdminUsecase, RemoveAdminUsecase removeAdminUsecase, BlockUserUsecase blockUserUsecase, UnBlockUserUsecase unBlockUserUsecase) {
        this.getAdminUsecase = getAdminUsecase;
        this.addAdminUsecase = addAdminUsecase;
        this.removeAdminUsecase = removeAdminUsecase;
        this.blockUserUsecase = blockUserUsecase;
        this.unBlockUserUsecase = unBlockUserUsecase;
    }

    @GetMapping
    public Flux<GetAdminResponse> getAll() {
        return getAdminUsecase.getAll();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Void> add(@RequestParam String email) {
        return addAdminUsecase.add(email);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> remove(@RequestParam String email) {
        return removeAdminUsecase.remove(email);
    }

    @PatchMapping("/users/block")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> block(@RequestParam String userId) {
        return blockUserUsecase.block(userId);
    }

    @PatchMapping("/users/unblock")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> unblock(@RequestParam String userId) {
        return unBlockUserUsecase.unblock(userId);
    }

}
