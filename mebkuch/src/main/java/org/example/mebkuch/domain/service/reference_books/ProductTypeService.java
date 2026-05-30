package org.example.mebkuch.domain.service.reference_books;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.mebkuch.domain.exception.ProductStatusException;
import org.example.mebkuch.domain.exception.ProductTypeException;
import org.example.mebkuch.domain.models.reference_book.ProductTypeModel;
import org.example.mebkuch.domain.repository.product.IProductRepository;
import org.example.mebkuch.domain.repository.reference_books.IProductTypeRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductTypeService {

    private final IProductTypeRepository iProductTypeRepository;
    private final IProductRepository productRepository;

    public ProductTypeModel create(ProductTypeModel model) {

        if (iProductTypeRepository.findByName(model.getName()).isPresent()) {
            throw new ProductTypeException(
                    "Type with name already exists: " + model.getName()
            );
        }

        return iProductTypeRepository.save(model);
    }


    public ProductTypeModel getById(Long id) {
        return iProductTypeRepository.findById(id)
                .orElseThrow(() ->
                        new ProductTypeException("Type not found with id: " + id)
                );
    }


    public Page<ProductTypeModel> getAll(Pageable pageable) {
        int maxSize = 50;
        Pageable safePageable = PageRequest.of(
                pageable.getPageNumber(),
                Math.min(pageable.getPageSize(), maxSize),
                pageable.getSort().isUnsorted() || pageable.getSort().isEmpty()
                        ? Sort.by("id").descending()
                        : pageable.getSort()
        );

        return iProductTypeRepository.findAll(safePageable);
    }


    public ProductTypeModel update(Long id, String newName) {
        return iProductTypeRepository.updateNameById(newName, id)
                .orElseThrow(() ->
                        new ProductTypeException("Error updating type with id: " + id)
                );
    }

    public ProductTypeModel getByName(String name){
        Optional<ProductTypeModel> productTypeModellOptional = iProductTypeRepository.findByName(name);

        if (productTypeModellOptional.isEmpty()){
            log.error("данный товар не найден по имени: {}", name);
            throw new ProductTypeException("данный товар не найден по имени");
        }

        return productTypeModellOptional.get();

    }


    public void delete(Long id) {

        if (productRepository.existsByTypeId(id)){
            log.error("нельзя удалить этот статус, к нему привязаны продукты");
            throw new ProductTypeException("нельзя удалить этот статус, к нему привязаны продукты");
        }

        boolean deleted = iProductTypeRepository.deleteById(id);

        if (!deleted) {
            log.error("Type not found with id: {}", id);
            throw new ProductTypeException("Type not found with id: " + id);
        }
    }

    public List<ProductTypeModel> getByNameContains(String nameContains) {
        return iProductTypeRepository.findByNameContains(nameContains);
    }

}