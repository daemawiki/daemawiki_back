package com.example.daemawiki.domain.content.api;

import com.example.daemawiki.domain.content.dto.AddContentRequest;
import com.example.daemawiki.domain.content.dto.WriteContentRequest;
import com.example.daemawiki.domain.content.service.AddContentTable;
import com.example.daemawiki.domain.content.service.WriteContent;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/documents/contents")
public class DocumentContentController {
    private final WriteContent writeContentService;
    private final AddContentTable addContentTableService;

    public DocumentContentController(WriteContent writeContentService, AddContentTable addContentTableService) {
        this.writeContentService = writeContentService;
        this.addContentTableService = addContentTableService;
    }

    @PatchMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> writeContent(@RequestBody WriteContentRequest request) {
        return writeContentService.execute(request);
    }

    @PatchMapping("/table")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> addContentTable(@RequestBody AddContentRequest request) {
        return addContentTableService.execute(request);
    }

}
