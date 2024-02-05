package com.example.daemawiki.domain.info.student.model;

import com.example.daemawiki.domain.document.model.DefaultDocument;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Document
@NoArgsConstructor
public class StudentInfo {

    @Id
    private String id;

    private String name;

    private Integer age;

    private Integer generation;

    private String gender;

    private PhysicalInfo physical;

    private String birthday;

    private String major;

    private String nickname;

    private DefaultDocument document;

    @Builder
    public StudentInfo(String name, Integer age, Integer generation, String gender, PhysicalInfo physicalInfo, String birthday, String major, String nickname, DefaultDocument document) {
        this.name = name;
        this.age = age;
        this.generation = generation;
        this.gender = gender;
        this.physical = physicalInfo;
        this.birthday = birthday;
        this.major = major;
        this.nickname = nickname;
        this.document = document;
    }

}
