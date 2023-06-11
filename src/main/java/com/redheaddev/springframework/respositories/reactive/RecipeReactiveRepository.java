package com.redheaddev.springframework.respositories.reactive;

import com.redheaddev.springframework.domain.Recipe;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

public interface RecipeReactiveRepository extends ReactiveMongoRepository<Recipe, String> {

    Mono<Recipe> findByDescription(String description);

}

