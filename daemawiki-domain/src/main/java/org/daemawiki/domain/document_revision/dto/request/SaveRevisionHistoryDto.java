package org.daemawiki.domain.document_revision.dto.request;

import org.daemawiki.domain.document_revision.model.RevisionDetail;
import org.daemawiki.domain.document_revision.model.type.RevisionType;

import java.util.List;

public record SaveRevisionHistoryDto(
        RevisionType type,
        String documentId,
        Long version,
        String title,
        List<RevisionDetail> data
) {

    public static SaveRevisionHistoryDto create(RevisionType type, String documentId, Long version, String title, List<RevisionDetail> data) {
        return new SaveRevisionHistoryDto(type, documentId, version, title, data);
    }

}
