package org.example.mebkuch.api.controller.category;

import lombok.RequiredArgsConstructor;
import org.example.mebkuch.api.dto.category.CategoryDto;
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
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private static final int LIMIT = 0;

    private final CategoryQueryUseCase categoryQueryUseCase;

    @GetMapping("/childs/{parent-id}")
    public ResponseEntity<List<CategoryDto>> getChildCategories(@PathVariable("parent-id") Long parentId){
        return ResponseEntity.ok(categoryQueryUseCase.geChildCategories(parentId, LIMIT));
    }

    @GetMapping("/descendants/{parent-id}")
    public ResponseEntity<List<CategoryDto>> getDescendantCategories(@PathVariable("parent-id") Long parentId){
        return ResponseEntity.ok(categoryQueryUseCase.getDescendantCategories(parentId, LIMIT));
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

    @GetMapping("/{parent-id}/childs")
    public ResponseEntity<Page<CategoryDto>> getChildCategories(@ParameterObject
                                                                @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC)
                                                                Pageable pageable, @PathVariable("parent-id") Long parentId){
        return ResponseEntity.ok(categoryQueryUseCase.getChildsDepth1(parentId, pageable));
    }

}