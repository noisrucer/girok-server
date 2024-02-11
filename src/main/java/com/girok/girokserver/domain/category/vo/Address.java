package com.girok.girokserver.domain.category.vo;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@NoArgsConstructor
@Getter
public class Address {

    private String city;
    private String street;
    private String zipcode;


}