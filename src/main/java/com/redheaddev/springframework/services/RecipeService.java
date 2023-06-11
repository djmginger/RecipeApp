package com.redheaddev.springframework.services;

import com.redheaddev.springframework.commands.RecipeCommand;
import com.redheaddev.springframework.domain.Recipe;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public interface RecipeService {

    Flux<Recipe> getRecipes();

    Mono<Recipe> findById(String l);

    Mono<RecipeCommand> findCommandById(String l);

    Mono<RecipeCommand> saveRecipeCommand(RecipeCommand command);

    Mono<Void> deleteById(String l);
}
