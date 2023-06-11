package com.redheaddev.springframework.services;

import com.redheaddev.springframework.commands.UnitOfMeasureCommand;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public interface UnitOfMeasureService {

    Mono<UnitOfMeasureCommand> findById(String l);
    Flux<UnitOfMeasureCommand> listAllUoms();
}