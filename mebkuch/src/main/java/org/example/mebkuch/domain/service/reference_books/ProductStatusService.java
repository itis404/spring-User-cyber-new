package org.example.mebkuch.domain.service.reference_books;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.mebkuch.domain.exception.ProductStatusException;
import org.example.mebkuch.domain.models.reference_book.ProductStatusModel;
import org.example.mebkuch.domain.repository.product.IProductRepository;
import org.example.mebkuch.domain.repository.reference_books.IProductStatusRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductStatusService {

    private final IProductStatusRepository repository;
    private final IProductRepository productRepository;

    public ProductStatusModel create(ProductStatusModel model) {

        if (repository.findByName(model.getName()).isPresent()) {
            log.error("Attempt to create duplicate status with name: {}", model.getName());
            throw new ProductStatusException(
                    "Status with name already exists: " + model.getName()
            );
        }

        return repository.save(model);
    }

    public ProductStatusModel getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() ->
                        new ProductStatusException("Status not found with id: " + id)
                );
    }

    public Page<ProductStatusModel> getAll(Pageable pageable) {
        int maxSize = 50;
        Pageable safePageable = PageRequest.of(
                pageable.getPageNumber(),
                Math.min(pageable.getPageSize(), maxSize),
                pageable.getSort().isUnsorted() || pageable.getSort().isEmpty()
                        ? Sort.by("id").descending()
                        : pageable.getSort()
        );
        return repository.findAll(safePageable);
    }

    public ProductStatusModel update(Long id, String newName) {
        return repository.updateNameById(id, newName)
                .orElseThrow(() ->
                        new ProductStatusException("Status not found with id: " + id)
                );
    }

    public void delete(Long id) {
        boolean deleted = repository.deleteById(id);

        if (productRepository.existsByStatusId(id)){
            throw new ProductStatusException("нельзя удалить этот статус, к нему привязаны продукты");
        }

        if (!deleted) {
            throw new ProductStatusException("Status not found with id: " + id);
        }
    }

    public List<ProductStatusModel> getByNameContains(String nameContains) {
        return repository.findByNameContains(nameContains);
    }
}