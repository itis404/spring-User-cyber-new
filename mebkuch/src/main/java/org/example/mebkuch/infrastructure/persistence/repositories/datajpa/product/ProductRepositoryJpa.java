package org.example.mebkuch.infrastructure.persistence.repositories.datajpa.product;

import org.example.mebkuch.infrastructure.persistence.entities.products.ProductEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepositoryJpa extends JpaRepository<ProductEntity, Long>, JpaSpecificationExecutor<ProductEntity> {

    @EntityGraph(attributePaths = {"sections"})
    @Query("""
    SELECT p
    FROM ProductEntity p
    JOIN p.sections s
    WHERE s.id = :sectionId
    """)
    Page<ProductEntity> findAllBySectionId(Long sectionId, Pageable pageable);

    @Query("""
    select (count(p) > 0)
    from ProductEntity p
    where p.productStyleEntity.id = :styleId
""")
    boolean existsByProductStyleId(@Param("styleId") Long styleId);

    @Query("""
    select (count(p) > 0)
    from ProductEntity p
    where p.productStatusEntity.id = :statusId
""")
    boolean existsByProductStatusId(@Param("statusId") Long statusId);

    @Query("""
    select (count(p) > 0)
    from ProductEntity p
    where p.productStatusEntity.id = :typeId
""")
    boolean existsByProductTypeId(@Param("typeId")Long typeId);

    @Query("""
    select (count(p) > 0)
    from ProductEntity p
    where p.сategoryEntity.id = :categoryId
""")
    boolean existsByCategoryId(@Param("categoryId") Long categoryId);
}