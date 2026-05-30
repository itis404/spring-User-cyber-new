package org.example.mebkuch.infrastructure.persistence.repositories.datajpa.category;


import org.example.mebkuch.infrastructure.persistence.entities.category.CategoryTreeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryTreeRepositoryJpa extends JpaRepository<CategoryTreeEntity, Long> {

    @Query(value = """
        select	
        	*
        from category_tree
        where ancestor_id = :parentId;
    """, nativeQuery = true)
    List<CategoryTreeEntity> findAllByDescendantId(@Param("parentId") Long parentId);

}
