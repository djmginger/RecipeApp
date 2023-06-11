package com.redheaddev.springframework.commands;

import com.redheaddev.springframework.domain.Difficulty;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.URL;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class RecipeCommand {
    private String id;
    @NotBlank(message="Description cannot be blank")
    @Size(min = 3, max = 255, message="${validatedValue} length must be between {min} and {max} characters.")
    private String description;

    @Min(value = 1, message = "Value must be blank or between {value} and 999")
    @Max(value = 999, message = "Value must be blank or between 1 and {value}")
    private Integer prepTime;

    @Min(value = 1, message = "Value must be between {value} and 999")
    @Max(value = 999, message = "Value must be blank or between 1 and {value}")
    private Integer cookTime;

    @Min(value = 1, message = "Value must be blank or between {value} and 999")
    @Max(value = 999, message = "Value must be blank or between 1 and {value}")
    private Integer servings;
    private String source;

    @URL(message = "Please provide a valid URL")
    private String url;
    private byte[] image;

    @NotBlank
    private String directions;
    private List<IngredientCommand> ingredients = new ArrayList<>();
    private Difficulty difficulty;
    private NotesCommand notes;
    private List<CategoryCommand> categories = new ArrayList<>();
}
