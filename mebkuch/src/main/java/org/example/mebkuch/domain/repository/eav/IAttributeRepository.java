package org.example.mebkuch.domain.repository.eav;


import org.example.mebkuch.domain.models.eav.AttributeModel;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IAttributeRepository {

    Optional<AttributeModel> findById(Long id);

    Optional<AttributeModel> findByName(String name);

    List<AttributeModel> findAll();

    AttributeModel save(AttributeModel attribute);

    boolean deleteById(Long id);

    boolean existsByName(String name);

}