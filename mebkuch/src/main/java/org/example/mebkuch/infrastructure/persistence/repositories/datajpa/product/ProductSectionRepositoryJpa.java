package org.example.mebkuch.infrastructure.persistence.repositories.datajpa.product;

import org.example.mebkuch.infrastructure.persistence.entities.reference_books.ProductSectionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductSectionRepositoryJpa extends JpaRepository<ProductSectionEntity, Long> {

    Optional<ProductSectionEntity> findByName(String name);

    @Modifying
    @Query("DELETE FROM ProductSectionEntity p WHERE p.id = :id")
    int deleteByIdReturnCount(@Param("id") Long id);

    List<ProductSectionEntity> findByNameContains(String nameContains);
}