package com.example.daemawiki.domain.user.model;

import com.example.daemawiki.domain.user.model.type.major.MajorType;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
public class UserDetail{

    private Integer gen;

    private MajorType major;

    private String club;

}
