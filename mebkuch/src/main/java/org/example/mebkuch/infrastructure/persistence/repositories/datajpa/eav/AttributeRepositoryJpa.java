package org.example.mebkuch.infrastructure.persistence.repositories.datajpa.eav;

import org.example.mebkuch.infrastructure.persistence.entities.eav.AttributeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AttributeRepositoryJpa extends JpaRepository<AttributeEntity, Long> {
    Optional<AttributeEntity> findByName(String name);

    boolean existsByName(String name);
}
