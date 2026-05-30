package org.example.mebkuch.api.controller.reference_books;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.mebkuch.api.dto.reference_books.ProductTypeDto;
import org.example.mebkuch.application.usecase.reference_books.type.ProductTypeQueryUseCase;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/product-type")
@RequiredArgsConstructor
@Slf4j
public class ProductTypeController {

    private final ProductTypeQueryUseCase productTypeQueryUseCase;

    @GetMapping
    public Page<ProductTypeDto> getAll(@ParameterObject
                                        @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC)
                                        Pageable pageable) {

        return productTypeQueryUseCase.getAll(pageable);
    }


    @GetMapping("/{id}")
    public ResponseEntity<ProductTypeDto> getById(@PathVariable Long id) {

        ProductTypeDto productTypeDto = productTypeQueryUseCase.getById(id);

        return ResponseEntity.ok().body(productTypeDto);
    }

    @GetMapping("/find/{name-contains}")
    public ResponseEntity<List<ProductTypeDto>> getByNameContains(@PathVariable("name-contains") String nameContains) {
        return ResponseEntity.ok().body(productTypeQueryUseCase.getByNameContains(nameContains));
    }
}