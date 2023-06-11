package com.redheaddev.springframework.domain;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Document
public class UnitOfMeasure {

    @Id
    private String id;
    private String description;
}
