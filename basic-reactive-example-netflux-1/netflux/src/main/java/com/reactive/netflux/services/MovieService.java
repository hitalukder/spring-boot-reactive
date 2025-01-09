package com.reactive.netflux.services;

import com.reactive.netflux.model.Movie;
import com.reactive.netflux.model.MovieEvent;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface MovieService {
    Mono<Movie> getMovieById(String id);

    Flux<Movie> getAllMovies();

    Flux<MovieEvent> streamMovieEvents(String id);
}
