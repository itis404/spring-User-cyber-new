package org.example.mebkuch.domain.repository.category;

import org.example.mebkuch.domain.models.category.CategoryModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ICategoryRepository {
    CategoryModel saveCategoryParent(CategoryModel categoryModelParent);
    CategoryModel saveCategoryChild(CategoryModel categoryModelChildren, Long parentId);
    Optional<CategoryModel> findCategoryModelByName(String name);
    List<CategoryModel> getDescendantCategories(Long parentId, Integer depth);
    Optional<CategoryModel> findById(Long id);
    Optional<CategoryModel> updateNameById(Long id, String name);
    boolean deleteCategory(Long id);
    Page<CategoryModel> getAllCategories(Pageable pageable);
    Page<CategoryModel> getRootCategories(Pageable pageable);
    Page<CategoryModel> getChildsDepth1(Long parentId, Pageable pageable);
}