package org.example.mebkuch.infrastructure.persistence.repositories.category;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.mebkuch.domain.models.category.CategoryModel;
import org.example.mebkuch.domain.models.category.CategoryTreeModel;
import org.example.mebkuch.domain.repository.category.ICategoryTreeRepository;
import org.example.mebkuch.infrastructure.persistence.entities.category.CategoryEntity;
import org.example.mebkuch.infrastructure.persistence.entities.category.CategoryTreeEntity;
import org.example.mebkuch.infrastructure.persistence.entities.category.CategoryTreeId;
import org.example.mebkuch.infrastructure.persistence.mapper.category.CategoryTreeModelEntityMapper;
import org.example.mebkuch.infrastructure.persistence.repositories.datajpa.category.CategoryRepositoryJpa;
import org.example.mebkuch.infrastructure.persistence.repositories.datajpa.category.CategoryTreeRepositoryJpa;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.util.List;

@Slf4j
@Repository
@RequiredArgsConstructor
@Primary
public class CategoryTreeRepositoryImpl implements ICategoryTreeRepository {
    private final CategoryTreeRepositoryJpa categoryTreeRepositoryJpa;
    private final CategoryRepositoryJpa categoryRepositoryJpa;

    @Override
    public CategoryTreeModel save(CategoryModel categoryModelParent, CategoryModel categoryModelChild, int depth) {

        CategoryEntity categoryEntityParent = categoryRepositoryJpa.getReferenceById(categoryModelParent.getId());
        CategoryEntity categoryEntityChild =  categoryRepositoryJpa.getReferenceById(categoryModelChild.getId());

        CategoryTreeEntity categoryTreeEntity = CategoryTreeEntity.builder()
                .id(new CategoryTreeId(categoryEntityParent.getId(), categoryEntityChild.getId()))
                .ancestor(categoryEntityParent)
                .descendant(categoryEntityChild)
                .build();

        return CategoryTreeModelEntityMapper.toModel(categoryTreeRepositoryJpa.save(categoryTreeEntity));
    }

    @Override
    public void addCategoryToTree(Long parentId, Long newChildId) {

        List<CategoryTreeEntity> parentPaths =
                categoryTreeRepositoryJpa.findAllByDescendantId(parentId);

        for (CategoryTreeEntity cte : parentPaths) {

            Long ancestorId = cte.getAncestor().getId();
            int depth = cte.getDepth() + 1;

            categoryTreeRepositoryJpa.save(
                    CategoryTreeEntity.builder()
                            .id(new CategoryTreeId(ancestorId, newChildId))
                            .depth(depth)
                            .build()
            );
        }

    }

}
