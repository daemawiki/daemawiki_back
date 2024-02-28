package com.example.daemawiki.domain.document.model;

import lombok.Builder;

@Builder
public record Editor(String email, String id) {
}
