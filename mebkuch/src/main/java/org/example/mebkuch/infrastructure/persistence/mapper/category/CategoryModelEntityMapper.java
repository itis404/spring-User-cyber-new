package org.example.mebkuch.infrastructure.persistence.mapper.category;

import org.example.mebkuch.domain.models.category.CategoryModel;
import org.example.mebkuch.infrastructure.persistence.entities.category.CategoryEntity;

import java.util.ArrayList;

public class CategoryModelEntityMapper {
    public static CategoryEntity toEntityWithoutChilds(CategoryModel categoryModel){
        return CategoryEntity.builder()
                .id(categoryModel.getId())
                .name(categoryModel.getName())
                .children(new ArrayList<>())
                .build();
    }

    public static CategoryModel toModelWithoutChilds(CategoryEntity categoryEntity){

        CategoryModel categoryModel = CategoryModel.builder()
                .id(categoryEntity.getId())
                .name(categoryEntity.getName())
                .build();

        if (categoryEntity.getParent() == null){
            return categoryModel;
        }

        categoryModel.setParentId(categoryEntity.getParent().getId());

        return categoryModel;
    }
}
