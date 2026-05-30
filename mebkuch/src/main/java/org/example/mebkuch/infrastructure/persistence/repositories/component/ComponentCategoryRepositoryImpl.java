package org.example.mebkuch.infrastructure.persistence.repositories.component;

import lombok.RequiredArgsConstructor;
import org.example.mebkuch.domain.exception.ComponentException;
import org.example.mebkuch.domain.models.component.ComponentCategoryModel;
import org.example.mebkuch.domain.repository.component.IComponentCategoryRepository;
import org.example.mebkuch.infrastructure.persistence.entities.component.ComponentCategoryEntity;
import org.example.mebkuch.infrastructure.persistence.mapper.component.ComponentCategoryModelEntityMapper;
import org.example.mebkuch.infrastructure.persistence.repositories.datajpa.component.ComponentCategoryJpa;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@Primary
@RequiredArgsConstructor
public class ComponentCategoryRepositoryImpl implements IComponentCategoryRepository {

    private final ComponentCategoryJpa componentCategoryJpa;

    @Override
    @Transactional
    public Optional<ComponentCategoryModel> save(ComponentCategoryModel componentCategoryModel) {

        ComponentCategoryEntity componentCategoryEntity = componentCategoryJpa.save(ComponentCategoryModelEntityMapper
                .toEntity(componentCategoryModel));

        return Optional.of(ComponentCategoryModelEntityMapper.toModel(componentCategoryEntity));
    }

    @Override
    public Optional<ComponentCategoryModel> findById(Long id) {
        ComponentCategoryEntity componentCategoryEntity = componentCategoryJpa
                .findById(id)
                .orElseThrow(() -> new ComponentException("категории с таким id не существует"));

        return Optional.of(ComponentCategoryModelEntityMapper.toModel(componentCategoryEntity));
    }

    @Override
    @Transactional
    public Optional<ComponentCategoryModel> updateNameById(Long id, String name){
        ComponentCategoryEntity componentCategoryEntity = componentCategoryJpa.findById(id)
                .orElseThrow(() -> new ComponentException("категории с таким id не существует"));

        componentCategoryEntity.setName(name);

        return Optional.of(ComponentCategoryModelEntityMapper.toModel(componentCategoryEntity));
    }

    @Override
    public Optional<ComponentCategoryModel> findByName(String name) {
        return Optional.empty();
    }

    @Override
    public Page<ComponentCategoryModel> findAll(Pageable pageable) {
        return componentCategoryJpa.findAll(pageable).map(ComponentCategoryModelEntityMapper::toModel);
    }

    @Override
    @Transactional
    public boolean deleteById(Long id) {
        return componentCategoryJpa.deleteByIdReturnCount(id) > 0;
    }
}
