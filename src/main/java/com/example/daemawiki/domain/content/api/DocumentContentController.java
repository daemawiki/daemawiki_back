package com.example.daemawiki.domain.content.api;

import com.example.daemawiki.domain.content.dto.AddContentRequest;
import com.example.daemawiki.domain.content.dto.DeleteContentRequest;
import com.example.daemawiki.domain.content.dto.EditContentTableTitleRequest;
import com.example.daemawiki.domain.content.dto.WriteContentRequest;
import com.example.daemawiki.domain.content.service.AddContentTable;
import com.example.daemawiki.domain.content.service.DeleteContent;
import com.example.daemawiki.domain.content.service.EditContentTableTitle;
import com.example.daemawiki.domain.content.service.WriteContent;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/documents/{documentId}/contents")
public class DocumentContentController {
    private final WriteContent writeContentService;
    private final AddContentTable addContentTableService;
    private final DeleteContent deleteContentService;
    private final EditContentTableTitle editContentTableTitleService;

    public DocumentContentController(WriteContent writeContentService, AddContentTable addContentTableService, DeleteContent deleteContentService, EditContentTableTitle editContentTableTitleService) {
        this.writeContentService = writeContentService;
        this.addContentTableService = addContentTableService;
        this.deleteContentService = deleteContentService;
        this.editContentTableTitleService = editContentTableTitleService;
    }

    @PatchMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> writeContent(@Valid @RequestBody WriteContentRequest request, @NotBlank @PathVariable String documentId) {
        return writeContentService.execute(request, documentId);
    }

    @PatchMapping("/table")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> addContentTable(@Valid @RequestBody AddContentRequest request, @NotBlank @PathVariable String documentId) {
        return addContentTableService.execute(request, documentId);
    }

    @PatchMapping("/remove")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteContent(@Valid @RequestBody DeleteContentRequest request, @NotBlank @PathVariable String documentId) {
        return deleteContentService.execute(request, documentId);
    }

    @PatchMapping("/edit")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> editContentTitle(@Valid @RequestBody EditContentTableTitleRequest request, @PathVariable String documentId) {
        return editContentTableTitleService.execute(request, documentId);
    }

}
