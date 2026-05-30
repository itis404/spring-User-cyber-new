package org.example.mebkuch.infrastructure.persistence.repositories.component;

import lombok.RequiredArgsConstructor;
import org.example.mebkuch.domain.exception.ComponentException;
import org.example.mebkuch.domain.models.component.ComponentModel;
import org.example.mebkuch.domain.models.filter.ComponentFilter;
import org.example.mebkuch.domain.repository.component.IComponentRepository;
import org.example.mebkuch.infrastructure.persistence.entities.component.ComponentCategoryEntity;
import org.example.mebkuch.infrastructure.persistence.entities.component.ComponentEntity;
import org.example.mebkuch.infrastructure.persistence.mapper.component.ComponentModelEntityMapper;
import org.example.mebkuch.infrastructure.persistence.repositories.datajpa.component.ComponentCategoryJpa;
import org.example.mebkuch.infrastructure.persistence.repositories.datajpa.component.ComponentRepositoryJpa;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
@Primary
public class ComponentRepositoryImpl implements IComponentRepository {

    private final ComponentRepositoryJpa componentRepositoryJpa;
    private final ComponentCategoryJpa componentCategoryRepositoryJpa;

    @Override
    @Transactional
    public Optional<ComponentModel> save(ComponentModel componentModel) {
        ComponentEntity componentEntity = componentRepositoryJpa
                .save(ComponentModelEntityMapper.toEntity(componentModel, componentCategoryRepositoryJpa));

        return Optional.of(ComponentModelEntityMapper.toModel(componentEntity));
    }

    @Override
    public Optional<ComponentModel> findById(Long id) {
        ComponentEntity componentEntity = componentRepositoryJpa
                .findById(id)
                .orElseThrow(() -> new ComponentException("не найден по данному id"));

        return Optional.of(ComponentModelEntityMapper.toModel(componentEntity));
    }

    @Override
    public Optional<ComponentModel> findByName(String name) {
        Optional<ComponentEntity> componentEntityOptional = componentRepositoryJpa
                .findByName(name);

        if (componentEntityOptional.isEmpty()){
            return Optional.empty();
        }

        return Optional.of(ComponentModelEntityMapper.toModel(componentEntityOptional.get()));
    }

    @Override
    @Transactional
    public Optional<ComponentModel> updateComponentAllFields(ComponentModel model) {

        ComponentEntity entity = componentRepositoryJpa.findById(model.getId())
                .orElseThrow(() -> new ComponentException("Component not found"));

        if (model.getName() != null) {
            entity.setName(model.getName());
        }

        if (model.getCategoryId() != null) {
            ComponentCategoryEntity componentCategoryEntity = componentCategoryRepositoryJpa
                    .findById(model.getCategoryId())
                    .orElseThrow(() -> new ComponentException("такой категории не существует"));

            entity.setCategory(componentCategoryEntity);
        }

        if (model.getMaterial() != null) {
            entity.setMaterial(model.getMaterial());
        }

        if (model.getCountry() != null) {
            entity.setCountry(model.getCountry());
        }

        if (model.getCost() != null) {
            entity.setCost(model.getCost());
        }

        ComponentEntity saved = componentRepositoryJpa.save(entity);

        return Optional.of(ComponentModelEntityMapper.toModel(saved));
    }

    @Override
    public List<ComponentModel> findAll() {
        return componentRepositoryJpa.findAll().stream().map(ComponentModelEntityMapper::toModel).toList();
    }

    @Override
    public List<ComponentModel> findTopN(int size) {
        return componentRepositoryJpa.findTopN(size).stream().map(ComponentModelEntityMapper::toModel).toList();
    }

    @Override
    public List<ComponentModel> getBatch(long begin, int size) {
        return componentRepositoryJpa.getBatch(begin, size).stream().map(ComponentModelEntityMapper::toModel).toList();
    }

    @Override
    public Page<ComponentModel> getComponentsBy(ComponentFilter componentFilter, Pageable pageable) {
        Specification<ComponentEntity> specification = getFullSpecification(componentFilter);

        return componentRepositoryJpa.findAll(specification, pageable).map(ComponentModelEntityMapper::toModel);
    }


    @Override
    @Transactional
    public boolean deleteById(Long id) {
        return componentRepositoryJpa.deleteByIdReturnCount(id) > 0;
    }

    private Specification<ComponentEntity> getFullSpecification(ComponentFilter filter) {

        Specification<ComponentEntity> spec = Specification.unrestricted();

        if (filter.getName() != null && !filter.getName().isBlank()) {
            spec = spec.and((root, query, cb) ->
                    cb.like(cb.lower(root.get("name")),
                            "%" + filter.getName().toLowerCase() + "%"));
        }

        if (filter.getMaterial() != null && !filter.getMaterial().isBlank()) {
            spec = spec.and((root, query, cb) ->
                    cb.like(cb.lower(root.get("material")),
                            "%" + filter.getMaterial().toLowerCase() + "%"));
        }

        if (filter.getCountry() != null && !filter.getCountry().isBlank()) {
            spec = spec.and((root, query, cb) ->
                    cb.like(cb.lower(root.get("country")),
                            "%" + filter.getCountry().toLowerCase() + "%"));
        }

        if (filter.getBeginCost() != null) {
            spec = spec.and((root, query, cb) ->
                    cb.greaterThanOrEqualTo(root.get("cost"), filter.getBeginCost()));
        }

        if (filter.getEndCost() != null) {
            spec = spec.and((root, query, cb) ->
                    cb.lessThanOrEqualTo(root.get("cost"), filter.getEndCost()));
        }

        return spec;
    }
}