package com.cine.cineauthenticationservice.repository;

import com.cine.cineauthenticationservice.entity.User;
import com.cine.cineauthenticationservice.specification.BaseSpecification;
import com.cine.cineauthenticationservice.specification.UserSpecification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {
    default Optional<User> findByEmail(String email){
        return findOne(BaseSpecification.<User>build()
                .and(UserSpecification.hasEmail(email)));
    }
}
