package com.example.daemawiki.domain.document.model;

import lombok.Builder;

import java.util.List;

@Builder
public record Group(
        String root,
        List<String> classes
) {
}
