package com.redheaddev.springframework.services;

import com.redheaddev.springframework.commands.IngredientCommand;
import com.redheaddev.springframework.converters.IngredientCommandToIngredient;
import com.redheaddev.springframework.converters.IngredientToIngredientCommand;
import com.redheaddev.springframework.domain.Ingredient;
import com.redheaddev.springframework.domain.Recipe;
import com.redheaddev.springframework.respositories.reactive.RecipeReactiveRepository;
import com.redheaddev.springframework.respositories.reactive.UnitOfMeasureReactiveRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class IngredientServiceImpl implements IngredientService {

    private final IngredientToIngredientCommand ingredientToIngredientCommand;
    private final IngredientCommandToIngredient ingredientCommandToIngredient;
    private final RecipeReactiveRepository recipeReactiveRepository;
    private final UnitOfMeasureReactiveRepository unitOfMeasureReactiveRepository;

    public IngredientServiceImpl(IngredientToIngredientCommand ingredientToIngredientCommand,
                                 IngredientCommandToIngredient ingredientCommandToIngredient,
                                 RecipeReactiveRepository recipeReactiveRepository, UnitOfMeasureReactiveRepository unitOfMeasureReactiveRepository) {
        this.ingredientToIngredientCommand = ingredientToIngredientCommand;
        this.ingredientCommandToIngredient = ingredientCommandToIngredient;
        this.recipeReactiveRepository = recipeReactiveRepository;
        this.unitOfMeasureReactiveRepository = unitOfMeasureReactiveRepository;
    }

    @Override
    public Mono<IngredientCommand> findByRecipeIdAndIngredientId(String recipeId, String ingredientId) {

        return recipeReactiveRepository.findById(recipeId)
                .flatMapIterable(Recipe::getIngredients)
                .filter(ingredient -> ingredient.getId().equalsIgnoreCase(ingredientId))
                .single()
                .map(ingredient -> {
                    IngredientCommand command = ingredientToIngredientCommand.convert(ingredient);
                    command.setRecipeId(recipeId);
                    return command;
                });
    }

    @Override
    @Transactional
    public Mono<IngredientCommand> saveIngredientCommand(IngredientCommand command) {
        return recipeReactiveRepository.findById(command.getRecipeId())
                .flatMap(recipe -> {
                    Optional<Ingredient> ingredientOptional = recipe.getIngredients()
                            .stream()
                            .filter(ingredient -> ingredient.getId().equals(command.getId()))
                            .findFirst();

                    if (ingredientOptional.isPresent()) {
                        Ingredient ingredientFound = ingredientOptional.get();
                        ingredientFound.setDescription(command.getDescription());
                        ingredientFound.setAmount(command.getAmount());

                        return unitOfMeasureReactiveRepository.findById(command.getUom().getId())
                                .flatMap(uom -> {
                                    ingredientFound.setUom(uom);
                                    return recipeReactiveRepository.save(recipe);
                                });
                    } else {
                        Ingredient ingredient = ingredientCommandToIngredient.convert(command);
                        ingredient.setId(UUID.randomUUID().toString());
                        recipe.addIngredient(ingredient);

                        return recipeReactiveRepository.save(recipe)
                                .map(savedRecipe -> {
                                    // Return the savedRecipe instead of the original recipe (for the sake of clarity)
                                    return savedRecipe;
                                });
                    }
                })
                .flatMap(savedRecipe -> {
                    Optional<Ingredient> savedIngredientOptional = savedRecipe.getIngredients().stream()
                            .filter(recipeIngredients -> recipeIngredients.getId().equals(command.getId()))
                            .findFirst();

                    if (savedIngredientOptional.isEmpty()) {
                        savedIngredientOptional = savedRecipe.getIngredients().stream()
                                .filter(recipeIngredients -> recipeIngredients.getDescription().equals(command.getDescription()))
                                .filter(recipeIngredients -> recipeIngredients.getAmount().equals(command.getAmount()))
                                .filter(recipeIngredients -> recipeIngredients.getUom().getId().equals(command.getUom().getId()))
                                .findFirst();
                    }

                    IngredientCommand ingredientCommandSaved = ingredientToIngredientCommand.convert(savedIngredientOptional.get());
                    ingredientCommandSaved.setRecipeId(savedRecipe.getId());
                    return Mono.just(ingredientCommandSaved);
                });
    }

    @Override
    public Mono<Void> deleteIngredient(String recipeId, String ingredientId) {
        log.info("deleting ingredient {} from recipe {}", ingredientId, recipeId);
        return recipeReactiveRepository.findById(recipeId)
                .flatMap(recipe -> {
                    recipe.getIngredients().removeIf(ingredient -> ingredient.getId().equals(ingredientId));
                    return recipeReactiveRepository.save(recipe);
                })
                .doOnError(error -> log.error("error deleting ingredient {} from recipe {}", ingredientId, recipeId, error))
                .then();
    }
}
