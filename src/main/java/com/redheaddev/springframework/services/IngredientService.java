package com.redheaddev.springframework.services;

import com.redheaddev.springframework.commands.IngredientCommand;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public interface IngredientService {
    Mono<IngredientCommand> findByRecipeIdAndIngredientId(String recipeId, String ingredientId);

    Mono<IngredientCommand> saveIngredientCommand(IngredientCommand command);

    Mono<Void> deleteIngredient(String recipeId, String ingredientId);

}
