package org.daemawiki.domain.document_info.model;

import org.daemawiki.domain.file.model.File;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
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
