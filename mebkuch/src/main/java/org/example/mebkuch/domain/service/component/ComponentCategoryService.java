package org.example.mebkuch.domain.service.component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.mebkuch.domain.exception.ComponentException;
import org.example.mebkuch.domain.models.component.ComponentCategoryModel;
import org.example.mebkuch.domain.repository.component.IComponentCategoryRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ComponentCategoryService {
    private final IComponentCategoryRepository componentCategoryRepository;

    public ComponentCategoryModel createComponentCategoryModel(ComponentCategoryModel componentCategoryModel){
        if (componentCategoryRepository.findByName(componentCategoryModel.getName()).isPresent()){
            log.error("Component category with name '{}' already exists", componentCategoryModel.getName());
            throw new ComponentException("такая категория уже существует");
        }

        return componentCategoryRepository.save(componentCategoryModel)
                .orElseThrow(() -> new ComponentException("ошибка при сохранении"));
    }

    public ComponentCategoryModel findById(Long id){
        return componentCategoryRepository.findById(id)
                .orElseThrow(() -> new ComponentException("ошибка при поиске"));
    }

    public ComponentCategoryModel updateNameById(Long id, String name){
        return componentCategoryRepository.updateNameById(id, name)
                .orElseThrow(() -> new ComponentException("ошибка при обновлении"));
    }

    public ComponentCategoryModel findByName(String name){
        return componentCategoryRepository.findByName(name)
                .orElseThrow(() -> new ComponentException("ошибка при поиске по имени"));
    }

    public Page<ComponentCategoryModel> findAll(Pageable pageable){

        int maxSize = 50;

        Pageable safePageable = PageRequest.of(
                pageable.getPageNumber(),
                Math.min(pageable.getPageSize(), maxSize),
                pageable.getSort().isUnsorted()
                        ? Sort.by("id").descending()
                        : pageable.getSort()
        );

        return componentCategoryRepository.findAll(safePageable);
    }

    public void deleteById(Long id){
        boolean isDeleted = componentCategoryRepository.deleteById(id);

        if (!isDeleted){
            log.error("Failed to delete component category with id '{}'", id);
            throw new ComponentException("ошибка при удалении");
        }
    }

}
