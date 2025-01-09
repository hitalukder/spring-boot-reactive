package com.reactive.reactive_example.repository;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.reactive.reactive_example.domain.Person;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

public class PersonRepositoryImplTest {

    PersonRepositoryImpl personRepositoryImpl;

    @BeforeEach
    void setup() {
        personRepositoryImpl = new PersonRepositoryImpl();
    }

    @Test
    void testGetByIdBlock() {
        Mono<Person> personMono = personRepositoryImpl.getById(1);
        Person person = personMono.block();

        System.out.println(person.toString());
    }

    @Test
    void testGetByIdSubscribe() {
        Mono<Person> personMono = personRepositoryImpl.getById(1);
        StepVerifier.create(personMono).expectNextCount(1).verifyComplete();
        personMono.subscribe(person -> System.out.println(person.toString()));
    }

    @Test
    void testGetByIdMapFunction() {
        Mono<Person> personMono = personRepositoryImpl.getById(1);
        StepVerifier.create(personMono).expectNextCount(1).verifyComplete();
        personMono.map(person -> {
            return person.getFirstName();
        }).subscribe(name -> System.out.println(name));
    }

    @Test
    void testGetByIdNotFoundSubscribe() {
        Mono<Person> personMono = personRepositoryImpl.getById(9);
        StepVerifier.create(personMono).expectNextCount(0).verifyComplete();
        personMono.subscribe(person -> System.out.println(person.toString()));
    }

    @Test
    void testFindAllBlock() {
        Flux<Person> personFlux = personRepositoryImpl.findAll();

        Person person = personFlux.blockFirst();

        System.out.println(person.toString());
    }

    @Test
    void testFindAllSubscribe() {
        Flux<Person> personFlux = personRepositoryImpl.findAll();
        StepVerifier.create(personFlux).expectNextCount(4).verifyComplete();
        personFlux.subscribe(person -> System.out.println(person.toString()));
    }

    @Test
    void testFluxToListMono() {
        Flux<Person> personFlux = personRepositoryImpl.findAll();

        Mono<List<Person>> personListMono = personFlux.collectList();

        personListMono.subscribe(list -> {
            list.forEach(person -> {
                System.out.println(person.toString());
            });
        });
    }

    @Test
    void testFindPersonById() {
        Flux<Person> personFlux = personRepositoryImpl.findAll();

        final Integer id = 3;

        Mono<Person> personMono = personFlux.filter(person -> person.getId() == id).next();

        personMono.subscribe(person -> {
            System.out.println(person.toString());
        });
    }

    @Test
    void testFindPersonByIdNotFound() {
        Flux<Person> personFlux = personRepositoryImpl.findAll();

        final Integer id = 8;

        Mono<Person> personMono = personFlux.filter(person -> person.getId() == id).next();

        personMono.subscribe(person -> {
            System.out.println(person.toString());
        });
    }

    @Test
    void testFindPersonByIdNotFoundWithException() {
        Flux<Person> personFlux = personRepositoryImpl.findAll();

        final Integer id = 8;

        Mono<Person> personMono = personFlux.filter(person -> person.getId() == id).single();

        personMono.doOnError(throwable -> {
            System.out.println("I went boom");
        }).onErrorReturn(Person.builder().id(id).build())
                .subscribe(person -> {
                    System.out.println(person.toString());
                });
    }
}
