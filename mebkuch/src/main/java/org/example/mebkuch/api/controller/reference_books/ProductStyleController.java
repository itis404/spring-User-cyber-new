package org.example.mebkuch.api.controller.reference_books;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.mebkuch.api.dto.reference_books.ProductStyleDto;
import org.example.mebkuch.application.usecase.reference_books.style.ProductStyleQueryUseCase;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/product-style")
@RequiredArgsConstructor
@Slf4j
public class ProductStyleController {

    private final ProductStyleQueryUseCase productStyleQueryUseCase;

    @GetMapping
    public Page<ProductStyleDto> getAll(@ParameterObject
                                         @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC)
                                         Pageable pageable) {
        return productStyleQueryUseCase.getAll(pageable);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductStyleDto> getProductStyleById(@PathVariable Long id) {
        ProductStyleDto productStyleDto = productStyleQueryUseCase.getById(id);
        return ResponseEntity.ok().body(productStyleDto);
    }

    @GetMapping("/find/{name-contains}")
    public ResponseEntity<List<ProductStyleDto>> getByNameContains(@PathVariable("name-contains") String nameContains) {
        return ResponseEntity.ok().body(productStyleQueryUseCase.getByNameContains(nameContains));
    }
}