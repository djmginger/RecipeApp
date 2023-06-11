package com.redheaddev.springframework.respositories;

import com.redheaddev.springframework.bootstrap.RecipeBootstrap;
import com.redheaddev.springframework.respositories.reactive.CategoryReactiveRepository;
import com.redheaddev.springframework.respositories.reactive.RecipeReactiveRepository;
import com.redheaddev.springframework.respositories.reactive.UnitOfMeasureReactiveRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.test.StepVerifier;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@ExtendWith(SpringExtension.class)
@DataMongoTest
public class UnitOfMeasureRepositoryTestIT {

    @Autowired
    UnitOfMeasureReactiveRepository unitOfMeasureReactiveRepository;

    @Autowired
    CategoryReactiveRepository categoryReactiveRepository;

    @Autowired
    RecipeReactiveRepository recipeReactiveRepository;

    @BeforeEach
    public void setUp() throws Exception {

        recipeReactiveRepository.deleteAll();
        unitOfMeasureReactiveRepository.deleteAll();
        categoryReactiveRepository.deleteAll();

        RecipeBootstrap recipeBootstrap = new RecipeBootstrap(categoryReactiveRepository, recipeReactiveRepository, unitOfMeasureReactiveRepository);

        recipeBootstrap.onApplicationEvent(null);
    }

    @Test
    public void findByDescriptionTeaspoon() {
        StepVerifier.create(unitOfMeasureReactiveRepository.findByDescription("Teaspoon"))
                .assertNext(uom -> assertThat(uom.getDescription(), is("Teaspoon")))
                .expectComplete() // Add this line to expect the stream to complete
                .verify();;
    }

    @Test
    public void findByDescriptionCup() {
        StepVerifier.create(unitOfMeasureReactiveRepository.findByDescription("Cups"))
                .assertNext(uom -> assertThat(uom.getDescription(), is("Cups")))
                .expectComplete() // Add this line to expect the stream to complete
                .verify();;
    }
}