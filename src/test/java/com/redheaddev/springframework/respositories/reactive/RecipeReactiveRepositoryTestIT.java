package com.redheaddev.springframework.respositories.reactive;


import com.redheaddev.springframework.domain.Recipe;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import static org.junit.jupiter.api.Assertions.assertEquals;


@ExtendWith(SpringExtension.class)
@DataMongoTest
public class RecipeReactiveRepositoryTestIT {

    @Autowired
    RecipeReactiveRepository recipeReactiveRepository;

    @BeforeEach
    public void setUp() throws Exception {
        recipeReactiveRepository.deleteAll().block();
    }

    @Test
    public void testRecipeSave() throws Exception {
        Recipe recipe = new Recipe();
        recipe.setDescription("Tasty");

        recipeReactiveRepository.save(recipe).block();
        Long count = recipeReactiveRepository.count().block();
        assertEquals(Long.valueOf(1L), count);
    }
}