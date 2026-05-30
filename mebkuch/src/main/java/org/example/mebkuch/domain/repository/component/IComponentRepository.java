package org.example.mebkuch.domain.repository.component;

import org.example.mebkuch.domain.models.component.ComponentModel;
import org.example.mebkuch.domain.models.filter.ComponentFilter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IComponentRepository {
    Optional<ComponentModel> save(ComponentModel componentModel);
    Optional<ComponentModel> findById(Long id);

    Optional<ComponentModel> findByName(String name);

    Optional<ComponentModel> updateComponentAllFields(ComponentModel componentModel);

    List<ComponentModel> findAll();

    List<ComponentModel> findTopN(int size);

    List<ComponentModel> getBatch(long begin, int size);

    Page<ComponentModel> getComponentsBy(ComponentFilter componentFilter, Pageable pageable);

    boolean deleteById(Long id);
}
