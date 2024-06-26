package org.daemawiki.domain.document_content.api;

import org.daemawiki.domain.document_content.dto.AddContentRequest;
import org.daemawiki.domain.document_content.dto.DeleteContentRequest;
import org.daemawiki.domain.document_content.dto.EditContentTableTitleRequest;
import org.daemawiki.domain.document_content.dto.WriteContentRequest;
import org.daemawiki.domain.document_content.usecase.AddContentTableUsecase;
import org.daemawiki.domain.document_content.usecase.RemoveContentTableUsecase;
import org.daemawiki.domain.document_content.usecase.UpdateContentTableTitleUsecase;
import org.daemawiki.domain.document_content.usecase.WriteContentUsecase;
import org.daemawiki.domain.document_content.service.AddContentTableService;
import org.daemawiki.domain.document_content.service.RemoveContentTableService;
import org.daemawiki.domain.document_content.service.UpdateContentTableTitleService;
import org.daemawiki.domain.document_content.service.WriteContentService;
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
