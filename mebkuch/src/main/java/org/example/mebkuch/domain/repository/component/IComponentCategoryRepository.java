package org.example.mebkuch.domain.repository.component;

import org.example.mebkuch.domain.models.component.ComponentCategoryModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface IComponentCategoryRepository {

    Optional<ComponentCategoryModel> save(ComponentCategoryModel componentCategoryModel);

    Optional<ComponentCategoryModel> findById(Long id);

    Optional<ComponentCategoryModel> updateNameById(Long id, String name);

    Optional<ComponentCategoryModel> findByName(String name);

    Page<ComponentCategoryModel> findAll(Pageable pageable);

    boolean deleteById(Long id);
}
