package org.example.mebkuch.api.controller.admin;

import lombok.RequiredArgsConstructor;
import org.example.mebkuch.api.dto.category.CategoryDto;
import org.example.mebkuch.api.validation.CategoryValidatorDto;
import org.example.mebkuch.application.usecase.category.CategoryCommandUseCase;
import org.example.mebkuch.application.usecase.category.CategoryQueryUseCase;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/categories")
@RequiredArgsConstructor
public class AdminCategoryController {

    private final CategoryCommandUseCase categoryCommandUseCase;
    private final CategoryQueryUseCase categoryQueryUseCase;

    private final static int LIMIT = 0;

    @PostMapping("/parent")
    public ResponseEntity<CategoryDto> createParentCategory(@RequestBody CategoryDto categoryDto){
        CategoryValidatorDto.validate(categoryDto);
        return ResponseEntity.ok(categoryCommandUseCase.createParentCategory(categoryDto));
    }

    @PostMapping("/{parent-id}/child")
    public ResponseEntity<CategoryDto> createChildCategory(@PathVariable("parent-id") Long parentId,
                                                           @RequestBody CategoryDto categoryDto){
        CategoryValidatorDto.validate(categoryDto);
        return ResponseEntity.ok(categoryCommandUseCase.createChildCategory(parentId, categoryDto));
    }

    @PatchMapping("/{category-id}/name")
    public ResponseEntity<CategoryDto> renameCategory(@PathVariable("category-id") Long id,
                                                      @RequestParam("name") String name){
        CategoryValidatorDto.validateName(name);
        return ResponseEntity.ok(categoryCommandUseCase.renameCategory(id, name));
    }

    @DeleteMapping("/{category-id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable("category-id") Long id){
        categoryCommandUseCase.deleteCategory(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/childs/{parent-id}")
    public ResponseEntity<List<CategoryDto>> getChildCategories(@PathVariable("parent-id") Long parentId){
        return ResponseEntity.ok(categoryQueryUseCase.geChildCategories(parentId, LIMIT));
    }

    // получение детей родителя напрямую
    @GetMapping("/{parent-id}/childs")
    public ResponseEntity<Page<CategoryDto>> getChildCategories(@ParameterObject
                                                                    @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC)
                                                                    Pageable pageable, @PathVariable("parent-id") Long parentId){
        return ResponseEntity.ok(categoryQueryUseCase.getChildsDepth1(parentId, pageable));
    }

    @GetMapping("/descendants/{parent-id}")
    public ResponseEntity<List<CategoryDto>> getDescendantCategories(@PathVariable("parent-id") Long parentId){
        return ResponseEntity.ok(categoryQueryUseCase.geChildCategories(parentId, LIMIT));
    }

    @GetMapping
    public Page<CategoryDto> getAll(@ParameterObject
                                    @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC)
                                    Pageable pageable) {
        return categoryQueryUseCase.getAllCategories(pageable);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryDto> getCategoryById(@PathVariable Long id){
        return ResponseEntity.ok(categoryQueryUseCase.getCategoryById(id));
    }

    @GetMapping("/roots")
    public Page<CategoryDto> getRootCategories(
            @ParameterObject @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        return categoryQueryUseCase.getRootCategories(pageable);
    }

}