package com.example.daemawiki.domain.user.model;

import com.example.daemawiki.domain.user.model.type.MajorType;
import lombok.Builder;

@Builder
public record UserDetail(
        Integer gen,
        MajorType major
) {
}
