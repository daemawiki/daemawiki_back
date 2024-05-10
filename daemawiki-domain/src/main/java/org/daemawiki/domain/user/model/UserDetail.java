package org.daemawiki.domain.user.model;

import lombok.Builder;
import lombok.Getter;
import org.daemawiki.domain.user.model.type.major.MajorType;

@Getter
@Builder
public class UserDetail {

    private Integer gen;

    private MajorType major;

    private String club;

}
