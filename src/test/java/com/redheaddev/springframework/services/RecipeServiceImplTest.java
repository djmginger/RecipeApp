package com.redheaddev.springframework.services;

import com.redheaddev.springframework.commands.RecipeCommand;
import com.redheaddev.springframework.converters.RecipeCommandToRecipe;
import com.redheaddev.springframework.converters.RecipeToRecipeCommand;
import com.redheaddev.springframework.domain.Recipe;

import com.redheaddev.springframework.respositories.reactive.RecipeReactiveRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.util.AssertionErrors.assertNotNull;

/**
 * Created by jt on 6/17/17.
 */
public class RecipeServiceImplTest {

    RecipeServiceImpl recipeService;

    @Mock
    RecipeReactiveRepository recipeReactiveRepository;

    @Mock
    RecipeToRecipeCommand recipeToRecipeCommand;

    @Mock
    RecipeCommandToRecipe recipeCommandToRecipe;

    @BeforeEach
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        recipeService = new RecipeServiceImpl(recipeReactiveRepository, recipeCommandToRecipe, recipeToRecipeCommand);
    }

    @Test
    public void getRecipeByIdTest() throws Exception {
        //given
        Recipe recipe = new Recipe();
        recipe.setId("1");

        //when
        when(recipeReactiveRepository.findById(anyString())).thenReturn(Mono.just(recipe));

        //then
        Recipe recipeReturned = recipeService.findById("1").block();
        assertNotNull("Null recipe returned", recipeReturned);
        verify(recipeReactiveRepository, times(1)).findById(anyString());
        verify(recipeReactiveRepository, never()).findAll();
    }

    @Test
    public void getRecipesTest() throws Exception {
        //when
        when(recipeService.getRecipes()).thenReturn(Flux.just(new Recipe()));

        //then
        List<Recipe> recipes = recipeService.getRecipes().collectList().block();
        assertEquals(recipes.size(), 1);
        verify(recipeReactiveRepository, times(1)).findAll();
        verify(recipeReactiveRepository, never()).findById(anyString());
    }

    @Test
    public void getRecipeCommandByIdTest() throws Exception {
        //given
        Recipe recipe = new Recipe();
        recipe.setId("1");
        RecipeCommand recipeCommand = new RecipeCommand();
        recipeCommand.setId("1");

        //when
        when(recipeReactiveRepository.findById(anyString())).thenReturn(Mono.just(recipe));
        when(recipeToRecipeCommand.convert(any())).thenReturn(recipeCommand);

        //then
        RecipeCommand commandById = recipeService.findCommandById("1").block();
        assertNotNull("Null recipe returned", commandById);
        verify(recipeReactiveRepository, times(1)).findById(anyString());
        verify(recipeReactiveRepository, never()).findAll();
    }

    @Test
    public void testDeleteById() throws Exception {
        //given
        String idToDelete = "2";

        //when
        when(recipeReactiveRepository.deleteById(anyString())).thenReturn(Mono.empty());

        recipeService.deleteById(idToDelete);

        //then
        verify(recipeReactiveRepository, times(1)).deleteById(anyString());
    }

}