package com.redheaddev.springframework.controllers;

import com.redheaddev.springframework.commands.IngredientCommand;
import com.redheaddev.springframework.commands.RecipeCommand;
import com.redheaddev.springframework.commands.UnitOfMeasureCommand;
import com.redheaddev.springframework.services.IngredientService;
import com.redheaddev.springframework.services.RecipeService;
import com.redheaddev.springframework.services.UnitOfMeasureService;
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
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.Arrays;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;


@ExtendWith(SpringExtension.class)
@WebFluxTest(IngredientController.class)
@Import({ThymeleafAutoConfiguration.class})
public class IngredientControllerTest {

    @MockBean
    IngredientService ingredientService;

    @MockBean
    RecipeService recipeService;

    @MockBean
    UnitOfMeasureService unitOfMeasureService;

    @Autowired
    WebTestClient webTestClient;

    IngredientController ingredientController;

    UnitOfMeasureCommand testUom;
    IngredientCommand testIngredient;

    @BeforeEach
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        ingredientController = new IngredientController(recipeService, ingredientService, unitOfMeasureService);

        testUom = new UnitOfMeasureCommand();
        testUom.setDescription("Ounce");
        testUom.setId("66");

        testIngredient = new IngredientCommand();
        testIngredient.setId("289");
        testIngredient.setDescription("Salt");
        testIngredient.setAmount(BigDecimal.valueOf(3));
        testIngredient.setUom(testUom);
    }

    @Test
    public void testListIngredients() throws Exception {
        //given
        RecipeCommand recipeCommand = new RecipeCommand();
        recipeCommand.setDescription("Recipe description");
        recipeCommand.setIngredients(Arrays.asList(testIngredient));

        //when
        when(recipeService.findCommandById(anyString())).thenReturn(Mono.just(recipeCommand));

        //then
        webTestClient.get().uri("/recipe/1/ingredients")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .value(body -> {
                    assertThat(body, containsString("Salt"));
                    assertThat(body, containsString("3 Ounce"));
                });

        verify(recipeService, times(1)).findCommandById("1");
    }

    @Test
    public void testShowIngredients() throws Exception {
        //when
        when(ingredientService.findByRecipeIdAndIngredientId(anyString(), anyString())).thenReturn(Mono.just(testIngredient));

        //then
        webTestClient.get().uri("/recipe/1/ingredient/2/show")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .value(body -> {
                    assertThat(body, containsString("Salt"));
                    assertThat(body, containsString("3 Ounce"));
                });

        verify(ingredientService, times(1)).findByRecipeIdAndIngredientId("1", "2");
    }

    @Test
    public void testNewIngredientForm() throws Exception {
        //given
        String recipeId = "123";
        RecipeCommand recipeCommand = new RecipeCommand();
        recipeCommand.setId(recipeId);

        //when
        when(recipeService.findCommandById(anyString())).thenReturn(Mono.just(recipeCommand));
        when(unitOfMeasureService.listAllUoms()).thenReturn(Flux.empty());

        //then
        webTestClient.get()
                .uri("/recipe/123/ingredient/new")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .value(body -> {
                    assertThat(body, containsString("/recipe/" + recipeId));
                });

        verify(recipeService, times(1)).findCommandById(recipeId);

    }

    @Test
    public void testUpdateIngredientForm() throws Exception {
        //given
        String recipeId = "7";
        RecipeCommand recipeCommand = new RecipeCommand();
        recipeCommand.setId(recipeId);
        testIngredient.setRecipeId(recipeId);

        //when
        when(ingredientService.findByRecipeIdAndIngredientId(anyString(), anyString())).thenReturn(Mono.just(testIngredient));
        when(unitOfMeasureService.listAllUoms()).thenReturn(Flux.just(testUom));

        //then
        webTestClient.get()
                .uri("/recipe/7/ingredient/2/update")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .value(body -> {
                    assertThat(body, containsString("/recipe/" + recipeId));
                    assertThat(body, containsString(testIngredient.getId()));
                    assertThat(body, containsString(testUom.getDescription()));
                    assertThat(body, containsString(testUom.getId()));
                });
    }

    @Test
    public void testSaveOrUpdate() throws Exception {
        // given
        String ingredientId = "5123123123";
        String description = "Sugar";
        IngredientCommand ingredientCommand = new IngredientCommand();
        ingredientCommand.setDescription(description);
        ingredientCommand.setId(ingredientId);
        ingredientCommand.setRecipeId("12");

        //when
        when(ingredientService.saveIngredientCommand(any(IngredientCommand.class))).thenReturn(Mono.just(ingredientCommand));
        when(unitOfMeasureService.findById(anyString())).thenReturn(Mono.just(testUom));

        // then
        webTestClient.post()
                .uri("/recipe/12/ingredient")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters
                        .fromFormData("id", ingredientId)
                        .with("description", description)
                        .with("amount", "2")
                        .with("uom.id", "66"))
                .exchange()
                .expectStatus().is3xxRedirection()
                .expectHeader()
                .valueEquals("Location", "/recipe/12/ingredient/5123123123/show");

        ArgumentCaptor<IngredientCommand> captor = ArgumentCaptor.forClass(IngredientCommand.class);
        verify(ingredientService).saveIngredientCommand(captor.capture());
        IngredientCommand savedIngredient = captor.getValue();
        assertThat(savedIngredient.getId(), is(ingredientCommand.getId()));
        assertThat(savedIngredient.getDescription(), is(ingredientCommand.getDescription()));
        assertThat(savedIngredient.getRecipeId(), is(ingredientCommand.getRecipeId()));
    }

    @Test
    public void testDeleteIngredient() throws Exception {

        //when
        when(ingredientService.deleteIngredient(any(), any())).thenReturn(Mono.empty());

        //then
        webTestClient.get()
                .uri("/recipe/3/ingredient/4/delete")
                .exchange()
                .expectStatus().is3xxRedirection()
                .expectHeader()
                .valueEquals("Location", "/recipe/3/ingredients");

        verify(ingredientService).deleteIngredient("3", "4");

    }
}
