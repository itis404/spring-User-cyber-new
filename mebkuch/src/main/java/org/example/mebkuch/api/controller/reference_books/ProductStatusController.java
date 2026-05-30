package org.example.mebkuch.api.controller.reference_books;

import lombok.RequiredArgsConstructor;
import org.example.mebkuch.api.dto.reference_books.ProductStatusDto;
import org.example.mebkuch.application.usecase.reference_books.status.ProductStatusQueryUseCase;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/product-status")
public class ProductStatusController {

    private final ProductStatusQueryUseCase productStatusQueryUseCase;

    @GetMapping("/{id}")
    public ResponseEntity<ProductStatusDto> getById(@PathVariable Long id) {

        ProductStatusDto productStatusDto = productStatusQueryUseCase.getById(id);

        return ResponseEntity.ok().body(productStatusDto);
    }

    @GetMapping
    public Page<ProductStatusDto> getAll(@ParameterObject
                                             @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC)
                                             Pageable pageable) {
        return productStatusQueryUseCase.getAll(pageable);
    }

    @GetMapping("/find/{name-contains}")
    public ResponseEntity<List<ProductStatusDto>> getByNameContains(@PathVariable("name-contains") String nameContains) {
        return ResponseEntity.ok().body(productStatusQueryUseCase.getByNameContains(nameContains));
    }
}
