package com.redheaddev.springframework.controllers;

import com.redheaddev.springframework.commands.IngredientCommand;
import com.redheaddev.springframework.commands.UnitOfMeasureCommand;
import com.redheaddev.springframework.services.IngredientService;
import com.redheaddev.springframework.services.RecipeService;
import com.redheaddev.springframework.services.UnitOfMeasureService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import reactor.core.publisher.Mono;

@Slf4j
@Controller
public class IngredientController {

    private final RecipeService recipeService;
    private final IngredientService ingredientService;
    private final UnitOfMeasureService unitOfMeasureService;

    public IngredientController(RecipeService recipeService, IngredientService ingredientService, UnitOfMeasureService unitOfMeasureService) {
        this.recipeService = recipeService;
        this.ingredientService = ingredientService;
        this.unitOfMeasureService = unitOfMeasureService;
    }


    @GetMapping("/recipe/{recipeId}/ingredients")
    public String listIngredients(@PathVariable String recipeId, Model model){
        log.debug("Getting ingredient list for recipe id: " + recipeId);
        model.addAttribute("recipe", recipeService.findCommandById(recipeId));

        return "recipe/ingredient/list";
    }

    @GetMapping("recipe/{recipeId}/ingredient/{id}/show")
    public String showRecipeIngredient(@PathVariable String recipeId, @PathVariable String id, Model model){
        model.addAttribute("ingredient", ingredientService.findByRecipeIdAndIngredientId(recipeId, id));

        return "recipe/ingredient/show";
    }

    @GetMapping("recipe/{recipeId}/ingredient/new")
    public String newIngredient(@PathVariable String recipeId, Model model){

        Mono<IngredientCommand> ingredient = recipeService.findCommandById(recipeId)
                .map(recipeCommand -> {
                    IngredientCommand ingredientCommand = new IngredientCommand();
                    ingredientCommand.setRecipeId(recipeCommand.getId());
                    ingredientCommand.setUom(new UnitOfMeasureCommand());
                    return ingredientCommand;
                });
        model.addAttribute("ingredient", ingredient);
        model.addAttribute("uomList", unitOfMeasureService.listAllUoms());

        return "recipe/ingredient/ingredientform";
    }

    @GetMapping("recipe/{recipeId}/ingredient/{id}/update")
    public String updateRecipeIngredient(@PathVariable String recipeId, @PathVariable String id, Model model) {
        model.addAttribute("ingredient", ingredientService.findByRecipeIdAndIngredientId(recipeId, id));
        model.addAttribute("uomList", unitOfMeasureService.listAllUoms());

        return "recipe/ingredient/ingredientform";
    }

    @GetMapping("recipe/{recipeId}/ingredient/{id}/delete")
    public Mono<String> deleteIngredient(@PathVariable String recipeId, @PathVariable String id){
        log.debug("deleting ingredient id:" + id);
        return ingredientService.deleteIngredient(recipeId, id)
                .then(Mono.just("redirect:/recipe/" + recipeId + "/ingredients"));
    }

    @PostMapping("recipe/{recipeId}/ingredient")
    public Mono<String> saveOrUpdate(@ModelAttribute IngredientCommand ingredientCommand) {
        String uomId = ingredientCommand.getUom().getId();

        return unitOfMeasureService.findById(uomId)
                .flatMap(unitOfMeasureCommand -> {
                    ingredientCommand.getUom().setDescription(unitOfMeasureCommand.getDescription());
                    return ingredientService.saveIngredientCommand(ingredientCommand);
                })
                .flatMap(savedCommand -> Mono.just("redirect:/recipe/" + ingredientCommand.getRecipeId() + "/ingredient/" + savedCommand.getId() + "/show"));
    }
}