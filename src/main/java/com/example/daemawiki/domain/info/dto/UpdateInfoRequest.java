package com.example.daemawiki.domain.info.dto;

import com.example.daemawiki.domain.info.model.Info;

import java.util.List;

public record UpdateInfoRequest(
        String documentId,
        List<Info> infoList
) {
}
