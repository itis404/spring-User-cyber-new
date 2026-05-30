package org.example.mebkuch.domain.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.mebkuch.domain.exception.CategoryException;
import org.example.mebkuch.domain.models.category.CategoryModel;
import org.example.mebkuch.domain.repository.category.ICategoryRepository;
import org.example.mebkuch.domain.repository.category.ICategoryTreeRepository;
import org.example.mebkuch.domain.repository.product.IProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.xml.catalog.CatalogException;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryService {
    private final ICategoryRepository categoryRepository;
    private final ICategoryTreeRepository categoryTreeRepository;
    private final IProductRepository productRepository;

    @Transactional
    public CategoryModel createCategoryWithoutChildren(CategoryModel categoryModel){

        if (categoryModel.getParentId()!=null){
            log.error("категорию, которую вы хотите создать имеет родителя");
            throw new CategoryException("категорию, которую вы хотите создать имеет родителя");
        }

        if (categoryRepository.findCategoryModelByName(categoryModel.getName()).isPresent()){
            log.error("такая категория по названию уже существует");
            throw new CatalogException("такая категория по названию уже существует");
        }
        CategoryModel categoryModelFromMemory = categoryRepository.saveCategoryParent(categoryModel);

        addCategoryToTree(categoryModelFromMemory.getId(), categoryModelFromMemory.getId());

        return categoryModelFromMemory;
    }

    @Transactional
    public CategoryModel createCategoryChildren(Long parentId, CategoryModel categoryModelChildren){

        CategoryModel categoryModelChildFromMemory = createCategoryChildrenInternal(parentId, categoryModelChildren);
        log.info("ID OPEN:  " + categoryModelChildFromMemory.getId());
        addCategoryToTree(parentId, categoryModelChildFromMemory.getId());

        return categoryModelChildFromMemory;
    }

    // Родитель - дети (depth = 1), но Потомок - поколение N (depth = N)
    // возврашает потомков
    public List<CategoryModel> getDescendantCategories(Long parentId, Integer depth){
        return categoryRepository.getDescendantCategories(parentId, depth);
    }

    public Page<CategoryModel> getChildsDepth1(Long parentId, Pageable pageable){
        int maxSize = 50;
        Pageable safePageable = PageRequest.of(
                pageable.getPageNumber(),
                Math.min(pageable.getPageSize(), maxSize),
                pageable.getSort().isUnsorted() || pageable.getSort().isEmpty()
                        ? Sort.by("id").descending()
                        : pageable.getSort()
        );

        return categoryRepository.getChildsDepth1(parentId, pageable);
    }

    public boolean deleteCategory(Long id){

        if (productRepository.existsByCategoryId(id)){
            log.error("нельзя удалить эту категорию, к ней привязаны продукты");
            throw new CategoryException("нельзя удалить эту категорию, к ней привязаны продукты");
        }

        if (!categoryRepository.deleteCategory(id)){
            log.error("Ошибка при удалении");
            throw new CategoryException("Ошибка при удалении");
        }

        return true;
    }

    public Page<CategoryModel> getAllCategories(Pageable pageable){
        int maxSize = 50;
        Pageable safePageable = PageRequest.of(
                pageable.getPageNumber(),
                Math.min(pageable.getPageSize(), maxSize),
                pageable.getSort().isUnsorted() || pageable.getSort().isEmpty()
                        ? Sort.by("id").descending()
                        : pageable.getSort()
        );
        return categoryRepository.getAllCategories(pageable);
    }

    public CategoryModel getCategoryById(Long id){
        return categoryRepository.findById(id).orElseThrow(() -> new CategoryException("категория не найдена"));
    }

    @Transactional
    public CategoryModel renameCategory(Long id, String name){
        return categoryRepository.updateNameById(id, name).orElseThrow(() -> new CategoryException("категория не найдена"));
    }

    private CategoryModel createCategoryChildrenInternal(Long parentId, CategoryModel categoryModelChildren){
        CategoryModel categoryModelParentFromMemory = categoryRepository
                .findById(parentId).orElseThrow(() -> new CategoryException("такой родительской категории не существует"));

        CategoryModel categoryModelChildrenFromMemory = categoryRepository
                .saveCategoryChild(categoryModelChildren, categoryModelParentFromMemory.getId());

        return categoryModelChildrenFromMemory;
    }

    public Page<CategoryModel> getRootCategories(Pageable pageable) {
        return categoryRepository.getRootCategories(pageable);
    }

    private void addCategoryToTree(Long parentId, Long newChildId) {
        categoryTreeRepository.addCategoryToTree(parentId, newChildId);
    }
}
