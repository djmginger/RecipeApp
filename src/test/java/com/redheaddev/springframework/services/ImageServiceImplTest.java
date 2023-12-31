package com.redheaddev.springframework.services;

import com.redheaddev.springframework.domain.Recipe;
import com.redheaddev.springframework.respositories.reactive.RecipeReactiveRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ImageServiceImplTest {

    @Mock
    RecipeReactiveRepository recipeReactiveRepository;

    ImageService imageService;

    @BeforeEach
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        imageService = new ImageServiceImpl(recipeReactiveRepository);
    }

    @Test
    public void saveImageFile() throws Exception {
        // given
        String id = "1";

        Recipe recipe = new Recipe();
        recipe.setId(id);
        when(recipeReactiveRepository.findById(id)).thenReturn(Mono.just(recipe));
        when(recipeReactiveRepository.save(recipe)).thenReturn(Mono.empty());

        byte[] bytes = "Some test bytes".getBytes(StandardCharsets.UTF_8);
        DataBuffer buffer = new DefaultDataBufferFactory().wrap(bytes);

        // when
        imageService.saveImageFile(id, Flux.just(buffer)).block();

        // then
        ArgumentCaptor<Recipe> recipeCaptor = ArgumentCaptor.forClass(Recipe.class);
        verify(recipeReactiveRepository).save(recipeCaptor.capture());
        Recipe savedRecipe = recipeCaptor.getValue();
        assertEquals(bytes.length, savedRecipe.getImage().length);
    }

}