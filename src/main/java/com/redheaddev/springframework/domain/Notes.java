package com.redheaddev.springframework.domain;

import lombok.*;
import org.springframework.data.annotation.Id;

@Getter
@Setter
public class Notes {

    @Id
    private String id;
    private Recipe recipe;
    private String recipeNotes;

}