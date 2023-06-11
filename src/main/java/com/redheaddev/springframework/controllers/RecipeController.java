package com.redheaddev.springframework.controllers;

import com.redheaddev.springframework.commands.RecipeCommand;
import com.redheaddev.springframework.exceptions.NotFoundException;
import com.redheaddev.springframework.services.RecipeService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.WebExchangeBindException;
import reactor.core.publisher.Mono;

@Slf4j
@Controller
public class RecipeController {

    private static final String RECIPE_RECIPEFORM_URL = "recipe/recipeform";
    private final RecipeService recipeService;

    public RecipeController(RecipeService recipeService){
        this.recipeService = recipeService;
    }

    @GetMapping("/recipe/{id}/show")
    public Mono<String> showById(@PathVariable String id, Model model){
        return recipeService.findById(id)
                .doOnNext(recipe -> model.addAttribute("recipe", recipe))
                .map(rec -> "recipe/show")
                .switchIfEmpty(Mono.error(new NotFoundException("recipe not found: " + id)));
    }


    @GetMapping("/recipe/new")
    public String newRecipe(Model model){
        model.addAttribute("recipe", new RecipeCommand());

        return RECIPE_RECIPEFORM_URL;
    }

    @GetMapping("/recipe/{id}/update")
    public String updateRecipe(@PathVariable String id, Model model){
        model.addAttribute("recipe", recipeService.findCommandById(id));

        return RECIPE_RECIPEFORM_URL;
    }

    @PostMapping("/recipe")
    public Mono<String> saveOrUpdate(@Valid @ModelAttribute("recipe") Mono<RecipeCommand> command){

        return command
                .flatMap(recipeService::saveRecipeCommand)
                .map(recipe -> "redirect:/recipe/" + recipe.getId() + "/show")
                .doOnError(thr -> log.error("Error saving recipe"))
                .onErrorResume(WebExchangeBindException.class, thr -> Mono.just(RECIPE_RECIPEFORM_URL));
    }

    @GetMapping("recipe/{id}/delete")
    public String deleteById(@PathVariable String id){
        log.debug("Deleting id: " + id);
        recipeService.deleteById(id);
        return "redirect:/";
    }
}