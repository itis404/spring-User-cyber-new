package org.example.mebkuch.application.usecase.category;

import lombok.RequiredArgsConstructor;
import org.example.mebkuch.api.dto.category.CategoryDto;
import org.example.mebkuch.domain.models.category.CategoryModel;
import org.example.mebkuch.domain.service.CategoryService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class CategoryQueryUseCase {
    private final CategoryService categoryService;

    private final static int CHILDREN_DEPTH = 1;
    private final static int ALL_DESCENDANT_DEPTH = Integer.MAX_VALUE;


    public List<CategoryDto> geChildCategories(Long parentCategoryId, int limit){
        List<CategoryModel> categoryModelList = categoryService.getDescendantCategories(parentCategoryId, CHILDREN_DEPTH);

        return categoryModelList.stream()
                .map(categoryModel -> {
                    return CategoryDto.builder()
                            .id(categoryModel.getId())
                            .name(categoryModel.getName())
                            .parentId(categoryModel.getParentId())
                            .build();
                })
                .toList();
    }

    // получить все потомки категории
    public List<CategoryDto> getDescendantCategories(Long parentCategoryId, int limit){
        List<CategoryModel> categoryModelList = categoryService.getDescendantCategories(parentCategoryId, ALL_DESCENDANT_DEPTH);

        return categoryModelList.stream()
                .map(categoryModel -> {
                    return CategoryDto.builder()
                            .id(categoryModel.getId())
                            .name(categoryModel.getName())
                            .parentId(categoryModel.getParentId())
                            .build();
                })
                .toList();
    }

    // получить ДЕТЕЙ родителя
    public Page<CategoryDto> getChildsDepth1(Long parentCategoryId, Pageable pageable){
        Page<CategoryModel> categoryModelList = categoryService.getChildsDepth1(parentCategoryId, pageable);

        return categoryModelList.map(categoryModel -> {
            return CategoryDto.builder()
                    .id(categoryModel.getId())
                    .name(categoryModel.getName())
                    .parentId(categoryModel.getParentId())
                    .build();
        });
    }

    public Page<CategoryDto> getAllCategories(Pageable pageable){
        Page<CategoryModel> categoryModelList = categoryService.getAllCategories(pageable);

        return categoryModelList
                .map(categoryModel -> {
                    return CategoryDto.builder()
                            .id(categoryModel.getId())
                            .name(categoryModel.getName())
                            .parentId(categoryModel.getParentId())
                            .build();
                });
    }

    public CategoryDto getCategoryById(Long id){
        CategoryModel categoryModel = categoryService.getCategoryById(id);

        return CategoryDto.builder()
                .id(categoryModel.getId())
                .name(categoryModel.getName())
                .parentId(categoryModel.getParentId())
                .build();
    }

    public Page<CategoryDto> getRootCategories(Pageable pageable) {
        return categoryService.getRootCategories(pageable)
                .map(categoryModel -> CategoryDto.builder()
                        .id(categoryModel.getId())
                        .name(categoryModel.getName())
                        .parentId(categoryModel.getParentId())
                        .build());
    }
}
