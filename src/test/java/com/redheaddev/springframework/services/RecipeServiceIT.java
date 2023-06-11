package com.redheaddev.springframework.services;

import com.redheaddev.springframework.commands.RecipeCommand;
import com.redheaddev.springframework.converters.RecipeCommandToRecipe;
import com.redheaddev.springframework.converters.RecipeToRecipeCommand;
import com.redheaddev.springframework.domain.Recipe;
import com.redheaddev.springframework.respositories.reactive.RecipeReactiveRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;


/**
 * Created by jt on 6/21/17.
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest
public class RecipeServiceIT {

    public static final String NEW_DESCRIPTION = "New Description";

    @Autowired
    RecipeService recipeService;

    @Autowired
    RecipeReactiveRepository recipeReactiveRepository;

    @Autowired
    RecipeCommandToRecipe recipeCommandToRecipe;

    @Autowired
    RecipeToRecipeCommand recipeToRecipeCommand;

    @Test
    public void saveRecipeCommand() {
        // given
        Recipe testRecipe = recipeReactiveRepository.findAll().blockFirst();
        RecipeCommand testCommand = recipeToRecipeCommand.convert(testRecipe);

        // when
        String testDescription = "This is the test description";
        testCommand.setDescription(testDescription);
        RecipeCommand savedCommand = recipeService.saveRecipeCommand(testCommand).block();

        // then
        assertEquals(testDescription, savedCommand.getDescription());
        assertEquals(testCommand.getCategories().size(), savedCommand.getCategories().size());
        assertEquals(testCommand.getIngredients().size(), savedCommand.getIngredients().size());
    }
}
