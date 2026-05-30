package org.example.mebkuch.domain.service.eav;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.mebkuch.domain.exception.ProductException;
import org.example.mebkuch.domain.models.eav.AttributeModel;
import org.example.mebkuch.domain.repository.eav.IAttributeRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AttributeService {

    private final IAttributeRepository attributeRepository;

    public AttributeModel create(AttributeModel attribute) {


        if (attributeRepository.existsByName(attribute.getName())) {
            log.error("Attribute with name '{}' already exists", attribute.getName());
            throw new ProductException("Attribute with this name already exists");
        }

        return attributeRepository.save(attribute);
    }


    public boolean delete(Long id) {
        return attributeRepository.deleteById(id);
    }

    public AttributeModel getById(Long id) {
        return attributeRepository.findById(id)
                .orElseThrow(() -> new ProductException("Attribute not found"));
    }

    public List<AttributeModel> getAll() {
        return attributeRepository.findAll();
    }
}