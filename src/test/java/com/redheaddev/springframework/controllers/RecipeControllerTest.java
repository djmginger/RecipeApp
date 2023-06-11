package com.redheaddev.springframework.controllers;

import com.redheaddev.springframework.commands.NotesCommand;
import com.redheaddev.springframework.commands.RecipeCommand;
import com.redheaddev.springframework.domain.Difficulty;
import com.redheaddev.springframework.domain.Notes;
import com.redheaddev.springframework.domain.Recipe;
import com.redheaddev.springframework.services.RecipeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.thymeleaf.ThymeleafAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.core.publisher.Mono;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@WebFluxTest(RecipeController.class)
@Import({ThymeleafAutoConfiguration.class})
public class RecipeControllerTest {

    @MockBean
    RecipeService recipeService;

    RecipeController controller;

    @Autowired
    WebTestClient webTestClient;


    @BeforeEach
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        controller = new RecipeController(recipeService);
    }

    @Test
    public void testGetRecipe() throws Exception {
        //given
        Recipe recipe = new Recipe();
        recipe.setDescription("Pizza");
        recipe.setId("1");
        Notes notes = new Notes();
        String recipeNotes = "Test recipe notes";
        notes.setRecipeNotes(recipeNotes);
        recipe.setNotes(notes);

        //when
        when(recipeService.findById(anyString())).thenReturn(Mono.just(recipe));

        //then
        webTestClient.get().uri("/recipe/1/show")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .value(body -> {
                    assertThat(body, containsString("Pizza"));
                    assertThat(body, containsString(recipeNotes));
                });

        verify(recipeService, times(1)).findById("1");
    }

//Unsure how to refactor the following test to check View and Model attribute values. Does not seem to be any easy way to check these using webTestCLient

//    @Test
//    public void testGetNewRecipeForm() throws Exception {
//        RecipeCommand command = new RecipeCommand();
//
//        mockMvc.perform(get("/recipe/new"))
//                .andExpect(status().isOk())
//                .andExpect(view().name("recipe/recipeform"))
//                .andExpect(model().attributeExists("recipe"));
//    }

    @Test
    public void testPostNewRecipeForm() throws Exception {
        //given
        RecipeCommand command = new RecipeCommand();
        command.setId("2");
        command.setDescription("Pizza");
        command.setPrepTime(12);
        command.setCookTime(7);
        command.setDifficulty(Difficulty.EASY);
        command.setServings(8);
        command.setDirections("cooka da pie");

        //when
        when(recipeService.saveRecipeCommand(any())).thenReturn(Mono.just(command));

        //then
        webTestClient.post()
                .uri("/recipe")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters
                        .fromFormData("id", "2")
                        .with("description", "Pizza")
                        .with("prepTime", "12")
                        .with("cookTime", "7")
                        .with("difficulty", "EASY")
                        .with("servings", "8")
                        .with("directions", "cooka da pie"))
                .exchange()
                .expectStatus().is3xxRedirection()
                .expectHeader()
                .valueEquals("Location", "/recipe/2/show");

        ArgumentCaptor<RecipeCommand> captor = ArgumentCaptor.forClass(RecipeCommand.class);
        verify(recipeService).saveRecipeCommand(captor.capture());
        RecipeCommand savedRecipe = captor.getValue();
        assertThat(savedRecipe.getId(), is(command.getId()));
        assertThat(savedRecipe.getDescription(), is(command.getDescription()));
        assertThat(savedRecipe.getPrepTime(), is(command.getPrepTime()));
        assertThat(savedRecipe.getCookTime(), is(command.getCookTime()));
        assertThat(savedRecipe.getDifficulty(), is(command.getDifficulty()));
        assertThat(savedRecipe.getServings(), is(command.getServings()));
        assertThat(savedRecipe.getDirections(), is(command.getDirections()));
    }

    @Test
    public void testGetUpdateView() throws Exception {
        //given
        RecipeCommand command = new RecipeCommand();
        command.setId("5");
        command.setDescription("Sausage");
        NotesCommand notes = new NotesCommand();
        String recipeNotes = "Test recipe notes";
        notes.setRecipeNotes(recipeNotes);
        command.setNotes(notes);

        //when
        when(recipeService.findCommandById(anyString())).thenReturn(Mono.just(command));

        //then
        webTestClient.get()
                .uri("/recipe/5/update")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .value(body -> {
                    assertThat(body, containsString("/recipe"));
                    assertThat(body, containsString(command.getDescription()));
                    assertThat(body, containsString(notes.getRecipeNotes()));
                });
    }

    @Test
    public void testDeleteAction() throws Exception {
        //when
        when(recipeService.deleteById(any())).thenReturn(Mono.empty());

        //then
        webTestClient.get()
                .uri("/recipe/52/delete")
                .exchange()
                .expectStatus().is3xxRedirection()
                .expectHeader()
                .valueEquals("Location", "/");

        verify(recipeService, times(1)).deleteById("52");

    }
}
