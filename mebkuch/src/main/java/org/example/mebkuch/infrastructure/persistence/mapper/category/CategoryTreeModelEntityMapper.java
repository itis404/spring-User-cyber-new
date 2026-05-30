package org.example.mebkuch.infrastructure.persistence.mapper.category;

import org.example.mebkuch.domain.models.category.CategoryTreeModel;
import org.example.mebkuch.infrastructure.persistence.entities.category.CategoryTreeEntity;

public class CategoryTreeModelEntityMapper {

    public static CategoryTreeModel toModel(CategoryTreeEntity categoryTreeEntity){
        return CategoryTreeModel.builder()
                .ancestorId(categoryTreeEntity.getAncestor().getId())
                .descendantId(categoryTreeEntity.getDescendant().getId())
                .depth(categoryTreeEntity.getDepth())
                .build();
    }
}
