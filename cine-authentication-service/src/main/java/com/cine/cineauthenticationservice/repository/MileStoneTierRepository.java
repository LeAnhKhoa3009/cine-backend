package com.cine.cineauthenticationservice.repository;

import com.cine.cineauthenticationservice.entity.MileStoneTier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MileStoneTierRepository extends JpaRepository<MileStoneTier, Long> {
    Optional<MileStoneTier> findByCode(String code);
}
