package org.example.mebkuch.domain.service.reference_books;

import lombok.RequiredArgsConstructor;
import org.example.mebkuch.domain.exception.ProductSectionException;
import org.example.mebkuch.domain.models.product.ProductModel;
import org.example.mebkuch.domain.models.reference_book.ProductSectionModel;
import org.example.mebkuch.domain.repository.reference_books.IProductSectionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductSectionService {

    private final IProductSectionRepository repository;

    public ProductSectionModel create(ProductSectionModel model) {

    if (repository.findByName(model.getName()).isPresent()){
        throw new ProductSectionException("Section with name already exists: " + model.getName());
    }

    return repository.save(model);
    }

    public ProductSectionModel getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ProductSectionException("Section not found with id: " + id));
    }

    public Page<ProductSectionModel> getAll(Pageable pageable) {
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


    public ProductSectionModel update(Long id, String newName) {
        return repository.updateNameById(id, newName)
                .orElseThrow(() -> new ProductSectionException("не существует такой секции по введенному id"));
    }


    public void delete(Long id) {
        boolean deleted = repository.deleteById(id);

        if (!deleted) {
            throw new ProductSectionException("Section not found with id: " + id);
        }
    }

    public void addProductToSection(Long sectionId, Long productId) {
        repository.addProductToSection(sectionId, productId);
    }

    public void removeProductFromSection(Long sectionId, Long productId) {
        repository.removeProductFromSection(sectionId, productId);
    }

    public Page<ProductModel> getProductsBySection(Long sectionId, Pageable pageable) {
        int maxSize = 50;
        Pageable safePageable = PageRequest.of(
                pageable.getPageNumber(),
                Math.min(pageable.getPageSize(), maxSize),
                pageable.getSort().isUnsorted() || pageable.getSort().isEmpty()
                        ? Sort.by("id").descending()
                        : pageable.getSort()
        );
        return repository.getProductsBySection(sectionId, safePageable);
    }

    public List<ProductSectionModel> getByNameContains(String nameContains) {
        return repository.findByNameContains(nameContains);
    }
}