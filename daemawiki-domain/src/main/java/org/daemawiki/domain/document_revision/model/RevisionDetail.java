package org.daemawiki.domain.document_revision.model;

public record RevisionDetail(
        Boolean added,
        Boolean removed,
        String value
) {
}
