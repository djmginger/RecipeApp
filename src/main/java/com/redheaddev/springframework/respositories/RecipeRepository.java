package com.redheaddev.springframework.respositories;

import com.redheaddev.springframework.domain.Recipe;
import com.redheaddev.springframework.domain.UnitOfMeasure;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface RecipeRepository extends CrudRepository<Recipe, String> {

    Optional<UnitOfMeasure> findByDescription(String description);
}
