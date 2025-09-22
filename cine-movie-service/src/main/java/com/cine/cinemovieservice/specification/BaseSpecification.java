package com.cine.cinemovieservice.specification;

import org.springframework.data.jpa.domain.Specification;

public class BaseSpecification {

    public static <T> Specification<T> build() {
        return Specification.unrestricted();
    }

    public static <T> Specification<T> notDeleted() {
        return BaseSpecification.<T>build().and((root, query, criteriaBuilder) -> criteriaBuilder.isFalse(root.get("deleted")));
    }
}
