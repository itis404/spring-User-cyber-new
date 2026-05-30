package org.example.mebkuch.infrastructure.persistence.repositories.datajpa.category;

import org.example.mebkuch.infrastructure.persistence.entities.category.CategoryEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepositoryJpa extends JpaRepository<CategoryEntity, Long> {
    Optional<CategoryEntity> findByName(String name);

// возвращает предков
    @Query(value = """
        with cte_children as (
            select ancestor_id
            from category_tree
            where descendant_id = :descendantId and "depth" > 0 and "depth" <= :depth)
        select c.id, c.name, parent_id
        from category c
        where exists (select * from cte_children where ancestor_id = c.id);
    """, nativeQuery = true)
    List<CategoryEntity> findByDescendantAndDepth(
            @Param("descendantId") Long descendantId,
            @Param("depth") Integer depth
    );

    @Modifying
    @Query("DELETE FROM CategoryEntity c WHERE c.id = :id")
    int deleteByIdReturnCount(@Param("id") Long id);

    Page<CategoryEntity> findByParentIsNull(Pageable pageable);

    Page<CategoryEntity> findAllByParentId(Long parentId, Pageable pageable);
}
