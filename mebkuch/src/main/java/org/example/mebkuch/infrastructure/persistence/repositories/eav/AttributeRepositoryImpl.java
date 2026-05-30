package org.example.mebkuch.infrastructure.persistence.repositories.eav;

import lombok.RequiredArgsConstructor;
import org.example.mebkuch.domain.models.eav.AttributeModel;
import org.example.mebkuch.domain.repository.eav.IAttributeRepository;
import org.example.mebkuch.infrastructure.persistence.entities.eav.AttributeEntity;
import org.example.mebkuch.infrastructure.persistence.mapper.eav.AttributeModelEntityMapper;
import org.example.mebkuch.infrastructure.persistence.repositories.datajpa.eav.AttributeRepositoryJpa;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@Primary
@RequiredArgsConstructor
public class AttributeRepositoryImpl implements IAttributeRepository {

    private final AttributeRepositoryJpa attributeRepositoryJpa;

    @Override
    public Optional<AttributeModel> findById(Long id) {
        return attributeRepositoryJpa.findById(id)
                .map(AttributeModelEntityMapper::toModel);
    }

    @Override
    public Optional<AttributeModel> findByName(String name) {
        return attributeRepositoryJpa.findByName(name)
                .map(AttributeModelEntityMapper::toModel);
    }

    @Override
    public List<AttributeModel> findAll() {
        return attributeRepositoryJpa.findAll()
                .stream()
                .map(AttributeModelEntityMapper::toModel)
                .toList();
    }

    @Override
    public AttributeModel save(AttributeModel attribute) {
        AttributeEntity entity = AttributeModelEntityMapper.toEntity(attribute);
        return AttributeModelEntityMapper.toModel(
                attributeRepositoryJpa.save(entity)
        );
    }

    @Override
    public boolean deleteById(Long id) {
        if (!attributeRepositoryJpa.existsById(id)) return false;
        attributeRepositoryJpa.deleteById(id);
        return true;
    }


    @Override
    public boolean existsByName(String name) {
        return attributeRepositoryJpa.existsByName(name);
    }
}