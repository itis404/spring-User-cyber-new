package org.example.mebkuch.domain.service.component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.mebkuch.domain.exception.ComponentException;
import org.example.mebkuch.domain.models.component.ComponentModel;
import org.example.mebkuch.domain.models.filter.ComponentFilter;
import org.example.mebkuch.domain.repository.component.IComponentRepository;
import org.example.mebkuch.domain.repository.component.IProductComponentRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ComponentService {
    private final IComponentRepository componentRepository;
    private final IProductComponentRepository productComponentRepository;

    public ComponentModel save(ComponentModel componentModel){
        if (componentRepository.findByName(componentModel.getName()).isPresent()){
            log.warn("Попытка создать компонент с уже существующим именем: {}", componentModel.getName());
            throw new ComponentException("такой компонент уже существует");
        }

        return componentRepository.save(componentModel)
                .orElseThrow(() -> new ComponentException("ошибка при сохранении"));
    }

    public ComponentModel findById(Long id){
        return componentRepository.findById(id)
                .orElseThrow(() -> new ComponentException("ошибка при поиске"));
    }

    public ComponentModel findByName(String name){
        return componentRepository.findByName(name)
                .orElseThrow(() -> new ComponentException("не существует компонента по такому имени"));
    }

    public void deleteById(Long id){
        boolean isDeleted = componentRepository.deleteById(id);

        if (!isDeleted){
            log.warn("Попытка удалить компонент с id {}, который не существует", id);
            throw new ComponentException("ошибка при удалении (Возможно не найдено по такому id)");
        }
    }

    public ComponentModel updateComponentAllFields(ComponentModel componentModel){
        return componentRepository
                .updateComponentAllFields(componentModel)
                .orElseThrow(() -> new ComponentException("Ошибка при обновлении"));
    }


    public List<ComponentModel> getFixedBatch(Long cursor, int size){
        if (cursor == null){
            return componentRepository.findTopN(size);
        }
        return componentRepository.getBatch(cursor, size);
    }

    public Page<ComponentModel> getComponentModelsWithFilter(ComponentFilter componentFilter, Pageable pageable){

        int maxSize = 50;

        Pageable safePageable = PageRequest.of(
                pageable.getPageNumber(),
                Math.min(pageable.getPageSize(), maxSize),
                pageable.getSort().isUnsorted()
                        ? Sort.by("id").descending()
                        : pageable.getSort()
        );

        return componentRepository.getComponentsBy(componentFilter, safePageable);
    }

}