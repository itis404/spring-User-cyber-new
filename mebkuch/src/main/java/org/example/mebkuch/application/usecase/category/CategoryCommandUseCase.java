package org.example.mebkuch.application.usecase.category;

import lombok.RequiredArgsConstructor;
import org.example.mebkuch.api.dto.category.CategoryDto;
import org.example.mebkuch.domain.models.category.CategoryModel;
import org.example.mebkuch.domain.service.CategoryService;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CategoryCommandUseCase {

    private final CategoryService categoryService;

    public CategoryDto createParentCategory(CategoryDto categoryDto){
        CategoryModel categoryModel = CategoryModel.builder()
                .name(categoryDto.getName())
                .build();

        CategoryModel categoryModelFromMemory = categoryService.createCategoryWithoutChildren(categoryModel);

        return CategoryDto.builder()
                .id(categoryModelFromMemory.getId())
                .name(categoryModelFromMemory.getName())
                .build();
    }

    public CategoryDto createChildCategory(Long parentId, CategoryDto categoryDtoChild){
        CategoryModel categoryModel = categoryService.createCategoryChildren(parentId, CategoryModel.builder()
                .name(categoryDtoChild.getName())
                .build());

        return CategoryDto.builder()
                .id(categoryModel.getId())
                .name(categoryModel.getName())
                .parentId(categoryModel.getParentId())
                .build();
    }

    public CategoryDto renameCategory(Long id, String name){
        CategoryModel categoryModelFromMemory = categoryService.renameCategory(id, name);

        return CategoryDto.builder()
                .id(categoryModelFromMemory.getId())
                .name(categoryModelFromMemory.getName())
                .parentId(categoryModelFromMemory.getParentId())
                .build();
    }

    public void deleteCategory(Long id){
        categoryService.deleteCategory(id);
    }



}
