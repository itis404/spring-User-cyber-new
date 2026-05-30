package org.example.mebkuch.infrastructure.persistence.repositories.datajpa.component;

import org.example.mebkuch.infrastructure.persistence.entities.component.ComponentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ComponentRepositoryJpa extends JpaRepository<ComponentEntity, Long>, JpaSpecificationExecutor<ComponentEntity> {

    @Modifying
    @Query("DELETE FROM ComponentEntity c WHERE c.id = :id")
    int deleteByIdReturnCount(@Param("id") Long id);

    Optional<ComponentEntity> findByName(String name);

    @Query("""
    SELECT 
        c
    FROM ComponentEntity c
    ORDER BY c.id ASC
    LIMIT :size
    """)
    List<ComponentEntity> findTopN(int size);

    @Query("""
    SELECT 
        c
    FROM ComponentEntity c
    WHERE c.id > :begin
    ORDER BY c.id ASC 
    LIMIT :size_
    """)
    List<ComponentEntity> getBatch(long begin, int size_);
}
