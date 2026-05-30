package org.example.mebkuch.infrastructure.persistence.repositories.component;

import lombok.RequiredArgsConstructor;
import org.example.mebkuch.domain.repository.component.IProductComponentRepository;
import org.example.mebkuch.infrastructure.persistence.repositories.datajpa.component.ProductComponentJpa;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@Primary
@RequiredArgsConstructor
public class ProductComponentRepositoryImpl implements IProductComponentRepository {

}