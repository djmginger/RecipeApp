package com.redheaddev.springframework.controllers;

import com.redheaddev.springframework.domain.Recipe;
import com.redheaddev.springframework.services.RecipeService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.thymeleaf.ThymeleafAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;

import java.util.Objects;

import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@WebFluxTest(IndexController.class)
@Import({ThymeleafAutoConfiguration.class})
public class IndexControllerTest {

    IndexController indexController;

    @MockBean
    RecipeService recipeService;

    @Autowired
    WebTestClient webTestClient;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);

        indexController = new IndexController(recipeService);
    }

    @Test
    public void testWebClient() throws Exception {

        Recipe recipe = new Recipe();
        recipe.setDescription("Fajita");
        when(recipeService.getRecipes()).thenReturn(Flux.just(recipe));

        webTestClient.get()
                .uri("/")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .consumeWith(response -> {
                    String responseBody = Objects.requireNonNull(response.getResponseBody());
                    Assertions.assertTrue(responseBody.contains("Fajita"));
                });
        verify(recipeService, times(1)).getRecipes();
    }
}