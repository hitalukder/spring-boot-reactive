package com.reactive.netflux.repositories;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import com.reactive.netflux.model.Movie;

public interface MovieRepository extends ReactiveMongoRepository<Movie, String>{
    
}
