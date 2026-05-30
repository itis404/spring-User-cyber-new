package org.example.mebkuch.infrastructure.persistence.repositories.datajpa.component;

import org.example.mebkuch.infrastructure.persistence.entities.component.ProductComponentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductComponentJpa extends JpaRepository<ProductComponentEntity, Long> {

}