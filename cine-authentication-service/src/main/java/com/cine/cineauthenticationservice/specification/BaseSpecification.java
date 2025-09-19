package com.cine.cineauthenticationservice.specification;

import org.springframework.data.jpa.domain.Specification;

public class BaseSpecification{

    public static <T> Specification<T> build() {
        return (Specification<T>)Specification.unrestricted();
    }
}
