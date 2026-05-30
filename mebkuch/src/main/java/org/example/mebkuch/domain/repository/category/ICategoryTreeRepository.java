package org.example.mebkuch.domain.repository.category;

import org.example.mebkuch.domain.models.category.CategoryModel;
import org.example.mebkuch.domain.models.category.CategoryTreeModel;
import org.springframework.stereotype.Repository;

@Repository
public interface ICategoryTreeRepository {
    CategoryTreeModel save(CategoryModel categoryModelParent, CategoryModel categoryModelChild, int depth);
    void addCategoryToTree(Long parentId, Long newChildId);
}