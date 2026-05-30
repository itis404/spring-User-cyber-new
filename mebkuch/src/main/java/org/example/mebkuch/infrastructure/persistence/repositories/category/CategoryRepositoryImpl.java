package org.example.mebkuch.infrastructure.persistence.repositories.category;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.mebkuch.domain.exception.CategoryException;
import org.example.mebkuch.domain.models.category.CategoryModel;
import org.example.mebkuch.domain.repository.category.ICategoryRepository;
import org.example.mebkuch.infrastructure.persistence.entities.category.CategoryEntity;
import org.example.mebkuch.infrastructure.persistence.mapper.category.CategoryModelEntityMapper;
import org.example.mebkuch.infrastructure.persistence.repositories.datajpa.category.CategoryRepositoryJpa;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
@Primary
@RequiredArgsConstructor
public class CategoryRepositoryImpl implements ICategoryRepository {

    private final CategoryRepositoryJpa categoryRepositoryJpa;

    @Override
    public CategoryModel saveCategoryParent(CategoryModel categoryModelParent) {
        return CategoryModelEntityMapper.toModelWithoutChilds(
                categoryRepositoryJpa.save(CategoryModelEntityMapper
                .toEntityWithoutChilds(categoryModelParent)));
    }

    @Override
    @Transactional
    public CategoryModel saveCategoryChild(CategoryModel categoryModelChildren, Long parentId) {
        CategoryEntity parent = categoryRepositoryJpa.findById(parentId)
                .orElseThrow(() -> new CategoryException("родитель не найден"));

        CategoryEntity child = CategoryModelEntityMapper.toEntityWithoutChilds(categoryModelChildren);
        child.setParent(parent);

        CategoryEntity saved = categoryRepositoryJpa.save(child);

        return CategoryModelEntityMapper.toModelWithoutChilds(saved);
    }

    @Override
    public Optional<CategoryModel> findCategoryModelByName(String name) {
        Optional<CategoryEntity> categoryEntityOptional = categoryRepositoryJpa
                .findByName(name);

        if (categoryEntityOptional.isEmpty()){
            log.warn("Категория с именем {} не найдена", name);
            return Optional.empty();
        }
        CategoryModel categoryModelOptional = CategoryModelEntityMapper
                .toModelWithoutChilds(categoryEntityOptional.get());

        return Optional.of(categoryModelOptional);
    }


    @Override
    public List<CategoryModel> getDescendantCategories(Long parentId, Integer depth) {
        List<CategoryEntity> categoryEntityList = categoryRepositoryJpa.findByDescendantAndDepth(parentId, depth);

        return categoryEntityList.stream().map(categoryEntity -> CategoryModelEntityMapper.toModelWithoutChilds(categoryEntity)).toList();
    }

    @Override
    public Optional<CategoryModel> findById(Long id) {
        Optional<CategoryEntity> categoryEntityOptional = categoryRepositoryJpa.findById(id);
        if (categoryEntityOptional.isEmpty()){
            return Optional.empty();
        }
        return Optional.of(CategoryModelEntityMapper.toModelWithoutChilds(categoryEntityOptional.get()));
    }

    @Override
    @Transactional
    public Optional<CategoryModel> updateNameById(Long id, String name) {
        CategoryEntity categoryEntity = categoryRepositoryJpa.findById(id)
                .orElseThrow(() -> new CategoryException("категория не найдена"));

        categoryEntity.setName(name);

        return Optional.of(CategoryModelEntityMapper.toModelWithoutChilds(categoryEntity));
    }

    @Override
    @Transactional
    public boolean deleteCategory(Long id) {
        return categoryRepositoryJpa.deleteByIdReturnCount(id) > 0;
    }

    @Override
    public Page<CategoryModel> getAllCategories(Pageable pageable) {
        return categoryRepositoryJpa.findAll(pageable)
                .map(categoryEntity -> CategoryModelEntityMapper.toModelWithoutChilds(categoryEntity));
    }

    @Override
    public Page<CategoryModel> getRootCategories(Pageable pageable) {
        return categoryRepositoryJpa.findByParentIsNull(pageable)
                .map(CategoryModelEntityMapper::toModelWithoutChilds);
    }

    @Override
    public Page<CategoryModel> getChildsDepth1(Long parentId, Pageable pageable) {
        return categoryRepositoryJpa.findAllByParentId(parentId, pageable).map(CategoryModelEntityMapper::toModelWithoutChilds);
    }

}
