package com.reactive.brewery.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;

import com.reactive.brewery.web.model.BeerStyleEnum;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/*
@Entity
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Beer {

    /*
     * @Id
     * 
     * @GeneratedValue(strategy = GenerationType.UUID)
     * 
     * @UuidGenerator(style = UuidGenerator.Style.RANDOM)
     */
    @Id
    private Integer id;

    // @Version
    private Long version;

    private String beerName;
    private BeerStyleEnum beerStyle;
    private String upc;

    private Integer quantityOnHand;
    private BigDecimal price;

    // @CreationTimestamp
    // @Column(updatable = false)
    private LocalDateTime createdDate;

    // @UpdateTimestamp
    private LocalDateTime lastModifiedDate;
}
