package com.redheaddev.springframework.services;

import com.redheaddev.springframework.commands.RecipeCommand;
import com.redheaddev.springframework.converters.RecipeCommandToRecipe;
import com.redheaddev.springframework.converters.RecipeToRecipeCommand;
import com.redheaddev.springframework.domain.Recipe;
import com.redheaddev.springframework.respositories.reactive.RecipeReactiveRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class RecipeServiceImpl implements RecipeService{

    private final RecipeReactiveRepository recipeReactiveRepository;
    private final RecipeCommandToRecipe recipeCommandToRecipe;
    private final RecipeToRecipeCommand recipeToRecipeCommand;

    public RecipeServiceImpl(RecipeReactiveRepository recipeReactiveRepository, RecipeCommandToRecipe recipeCommandToRecipe, RecipeToRecipeCommand recipeToRecipeCommand) {
        this.recipeReactiveRepository = recipeReactiveRepository;
        this.recipeCommandToRecipe = recipeCommandToRecipe;
        this.recipeToRecipeCommand = recipeToRecipeCommand;
    }

    @Override
    public Flux<Recipe> getRecipes() {
        return recipeReactiveRepository.findAll();
    }

    @Override
    public Mono<Recipe> findById(String l){
        return recipeReactiveRepository.findById(l);
    }

    @Override
    public Mono<RecipeCommand> findCommandById(String id) {
        return recipeReactiveRepository.findById(id)
                .map(recipeToRecipeCommand::convert)
                .doOnError(thr -> log.warn("Recipe Not Found. ID value: " + id));
    }

    @Override
    @Transactional
    public Mono<RecipeCommand> saveRecipeCommand(RecipeCommand command) {
        Recipe detachedRecipe = recipeCommandToRecipe.convert(command);
        if (detachedRecipe.getId().isEmpty()) {
            detachedRecipe.setId(null);
        }

        return recipeReactiveRepository.save(detachedRecipe)
                .flatMap(recipe -> {
                    log.debug("Saved RecipeID:" + recipe.getId());
                    return Mono.just(recipeToRecipeCommand.convert(recipe));
                });
    }

    @Override
    public Mono<Void> deleteById(String id) {
        recipeReactiveRepository.deleteById(id).subscribe();

        return Mono.empty();
    }
}
