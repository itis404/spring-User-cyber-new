package org.example.mebkuch.infrastructure.persistence.repositories.datajpa.product;

import org.example.mebkuch.infrastructure.persistence.entities.reference_books.ProductTypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductTypeRepositoryJpa extends JpaRepository<ProductTypeEntity, Long> {

    Optional<ProductTypeEntity> findByName(String name);

    @Modifying
    @Query("DELETE FROM ProductTypeEntity p WHERE p.id = :id")
    int deleteByIdReturnCount(@Param("id") Long id);

    List<ProductTypeEntity> findByNameContains(String nameContains);
}