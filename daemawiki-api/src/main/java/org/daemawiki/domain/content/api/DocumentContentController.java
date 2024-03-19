package org.daemawiki.domain.content.api;

import org.daemawiki.domain.content.dto.AddContentRequest;
import org.daemawiki.domain.content.dto.DeleteContentRequest;
import org.daemawiki.domain.content.dto.EditContentTableTitleRequest;
import org.daemawiki.domain.content.dto.WriteContentRequest;
import org.daemawiki.domain.content.usecase.AddContentTableUsecase;
import org.daemawiki.domain.content.usecase.RemoveContentTableUsecase;
import org.daemawiki.domain.content.usecase.UpdateContentTableTitleUsecase;
import org.daemawiki.domain.content.usecase.WriteContentUsecase;
import org.daemawiki.domain.content.usecase.service.AddContentTableService;
import org.daemawiki.domain.content.usecase.service.RemoveContentTableService;
import org.daemawiki.domain.content.usecase.service.UpdateContentTableTitleService;
import org.daemawiki.domain.content.usecase.service.WriteContentService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/documents/{documentId}/contents")
public class DocumentContentController {
    private final WriteContentUsecase writeContentUsecase;
    private final AddContentTableUsecase addContentTableUsecase;
    private final RemoveContentTableUsecase removeContentTableUsecase;
    private final UpdateContentTableTitleUsecase updateContentTableTitleUsecase;

    public DocumentContentController(WriteContentService writeContentUsecase, AddContentTableService addContentTableUsecase, RemoveContentTableService removeContentTableUsecase, UpdateContentTableTitleService updateContentTableTitleUsecase) {
        this.writeContentUsecase = writeContentUsecase;
        this.addContentTableUsecase = addContentTableUsecase;
        this.removeContentTableUsecase = removeContentTableUsecase;
        this.updateContentTableTitleUsecase = updateContentTableTitleUsecase;
    }

    @PatchMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> writeContent(@Valid @RequestBody WriteContentRequest request, @NotBlank @PathVariable String documentId) {
        return writeContentUsecase.write(request, documentId);
    }

    @PatchMapping("/table")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> addContentTable(@Valid @RequestBody AddContentRequest request, @NotBlank @PathVariable String documentId) {
        return addContentTableUsecase.add(request, documentId);
    }

    @PatchMapping("/remove")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteContent(@Valid @RequestBody DeleteContentRequest request, @NotBlank @PathVariable String documentId) {
        return removeContentTableUsecase.remove(request, documentId);
    }

    @PatchMapping("/title/edit")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> editContentTitle(@Valid @RequestBody EditContentTableTitleRequest request, @PathVariable String documentId) {
        return updateContentTableTitleUsecase.update(request, documentId);
    }

}
