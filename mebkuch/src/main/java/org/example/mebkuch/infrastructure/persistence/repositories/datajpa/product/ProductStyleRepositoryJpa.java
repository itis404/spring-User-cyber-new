package org.example.mebkuch.infrastructure.persistence.repositories.datajpa.product;

import org.example.mebkuch.infrastructure.persistence.entities.reference_books.ProductStatusEntity;
import org.example.mebkuch.infrastructure.persistence.entities.reference_books.ProductStyleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductStyleRepositoryJpa extends JpaRepository<ProductStyleEntity, Long> {

    Optional<ProductStyleEntity> findByName(String name);

    @Modifying
    @Query("DELETE FROM ProductStyleEntity p where p.id = :id")
    int deleteByIdReturnCount(@Param("id") Long id);

    List<ProductStyleEntity> findByNameContains(String nameContains);
}
