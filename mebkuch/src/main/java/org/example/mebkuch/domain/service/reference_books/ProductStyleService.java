package org.example.mebkuch.domain.service.reference_books;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.mebkuch.domain.exception.ProductStyleException;
import org.example.mebkuch.domain.models.reference_book.ProductStatusModel;
import org.example.mebkuch.domain.models.reference_book.ProductStyleModel;
import org.example.mebkuch.domain.repository.product.IProductRepository;
import org.example.mebkuch.domain.repository.reference_books.IProductStyleRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductStyleService {

    private final IProductStyleRepository repository;
    private final IProductRepository productRepository;

    public ProductStyleModel create(ProductStyleModel model) {

        if (repository.findByName(model.getName()).isPresent()) {
            throw new ProductStyleException(
                    "Style with name already exists: " + model.getName()
            );
        }

        return repository.save(model);
    }

    public ProductStyleModel getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() ->
                        new ProductStyleException("Style not found with id: " + id)
                );
    }

    public Page<ProductStyleModel> getAll(Pageable pageable) {

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

    public ProductStyleModel update(Long id, String newName) {
        return repository.updateNameById(newName, id).orElseThrow(() -> new ProductStyleException("ошибка при обновлении"));
    }



    public void delete(Long id) {

        if (productRepository.existsBuStyleId(id)){
            log.error("Cannot delete style with id: {} because it is associated with existing products", id);
            throw new ProductStyleException("нельзя удалить этот стиль, так как к нему привязны продукты");
        }

        boolean deleted = repository.deleteById(id);

        if (!deleted) {
            log.error("Failed to delete style with id: {}", id);
            throw new ProductStyleException("Style not found with id: " + id);
        }
    }

    public List<ProductStyleModel> getByNameContains(String nameContains) {
        return repository.getByNameContains(nameContains);
    }
}