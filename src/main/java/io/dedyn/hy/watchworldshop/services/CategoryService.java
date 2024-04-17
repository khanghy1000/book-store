package io.dedyn.hy.watchworldshop.services;

import io.dedyn.hy.watchworldshop.entities.Category;
import io.dedyn.hy.watchworldshop.repositories.CategoryRepository;
import io.dedyn.hy.watchworldshop.utils.SlugifyUtil;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;

@Service
@Transactional
public class CategoryService {
    private final CategoryRepository categoryRepository;

    @Autowired
    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public List<Category> findAll() {
        return categoryRepository.findAll();
    }

    public Category findById(Integer id) {
        return categoryRepository.findById(id).orElse(null);
    }

    public Category save(Category category) {
        category.setSlug(SlugifyUtil.slugify(category.getName()));
        return categoryRepository.save(category);
    }

    public void delete(Integer id) {
        categoryRepository.deleteById(id);
    }

    public boolean isUniqueName(Category category) {
        Category dbCategory = categoryRepository.findFirstBySlug(SlugifyUtil.slugify(category.getName()));
        if (dbCategory == null) return true;
        if (category.getId() == null) return false;
        return category.getId().equals(dbCategory.getId());
    }
}
