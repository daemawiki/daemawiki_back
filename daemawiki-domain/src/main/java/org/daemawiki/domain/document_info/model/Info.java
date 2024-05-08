package org.daemawiki.domain.document_info.model;

import lombok.Builder;
import lombok.Getter;
import org.daemawiki.domain.file.model.File;

import java.util.List;

@Getter
@Builder
public class Info {

    private File documentImage;
    private String subTitle;

    private List<Detail> details;

    public void update(String subTitle, List<Detail> details) {
        this.subTitle = subTitle;
        this.details = details;
    }

}
