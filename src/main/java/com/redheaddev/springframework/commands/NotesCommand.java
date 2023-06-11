package com.redheaddev.springframework.commands;

import com.redheaddev.springframework.domain.Recipe;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class NotesCommand {
    private String id;
    private Recipe recipe;
    private String recipeNotes;
}
