package org.example.mebkuch.infrastructure.persistence.repositories.datajpa.component;

import org.example.mebkuch.infrastructure.persistence.entities.component.ComponentCategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ComponentCategoryJpa extends JpaRepository<ComponentCategoryEntity, Long> {

    @Modifying
    @Query("DELETE FROM ComponentCategoryEntity c WHERE c.id = :id")
    int deleteByIdReturnCount(@Param("id") Long id);
}
