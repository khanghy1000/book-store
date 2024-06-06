package io.dedyn.hy.watchworldshop.services;

import io.dedyn.hy.watchworldshop.entities.product.Product;
import io.dedyn.hy.watchworldshop.repositories.ProductRepository;
import io.dedyn.hy.watchworldshop.utils.SlugifyUtil;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class ProductService {
    private final ProductRepository productRepository;

    private final int PAGE_SIZE = 10;

    @Autowired
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<Product> findAll() {
        return productRepository.findAll();
    }

    public List<Product> findAll(Sort sort) {
        return productRepository.findAll(sort);
    }

    public Product findById(Long id) {
        return productRepository.findById(id).orElse(null);
    }

    public Product findBySlug(String slug) {
        return productRepository.findFirstBySlug(slug);
    }

    public Long count() {
        return productRepository.count();
    }

    public Product save(Product product) {
        product.setSlug(SlugifyUtil.slugify(product.getName()));
        return productRepository.save(product);
    }

    public void deleteById(Long id) {
        productRepository.deleteById(id);
    }

    public boolean isUniqueName(Product product) {
        Product dbProduct = productRepository.findFirstBySlug(SlugifyUtil.slugify((product.getName())));
        if (dbProduct == null) return true;
        if (product.getId() == null) return false;
        return product.getId().equals(dbProduct.getId());
    }

    public List<Product> findByKeyword(String keyword) {
        return productRepository.findByKeyword(keyword);
    }

    public Page<Product> findEnabledByKeyword(String keyword,
                                              Integer brandId,
                                              Integer categoryId,
                                              Integer page,
                                              String sortBy,
                                              String order) {
        if (page > 0) page--;
        if (page < 0) page = 0;

        if (brandId != null && brandId != 0 && categoryId != null && categoryId != 0) {
            if (order.equals("desc")) {
                return productRepository.findEnabledByKeywordAndBrandAndCategory(keyword, brandId, categoryId, PageRequest.of(page, PAGE_SIZE, Sort.by(sortBy).descending()));
            } else {
                return productRepository.findEnabledByKeywordAndBrandAndCategory(keyword, brandId, categoryId, PageRequest.of(page, PAGE_SIZE, Sort.by(sortBy).ascending()));
            }
        }

        if (brandId != null && brandId != 0) {
            if (order.equals("desc")) {
                return productRepository.findEnabledByKeywordAndBrand(keyword, brandId, PageRequest.of(page, PAGE_SIZE, Sort.by(sortBy).descending()));
            } else {
                return productRepository.findEnabledByKeywordAndBrand(keyword, brandId, PageRequest.of(page, PAGE_SIZE, Sort.by(sortBy).ascending()));
            }
        }

        if (categoryId != null && categoryId != 0) {
            if (order.equals("desc")) {
                return productRepository.findEnabledByKeywordAndCategory(keyword, categoryId, PageRequest.of(page, PAGE_SIZE, Sort.by(sortBy).descending()));
            } else {
                return productRepository.findEnabledByKeywordAndCategory(keyword, categoryId, PageRequest.of(page, PAGE_SIZE, Sort.by(sortBy).ascending()));
            }
        }

        if (order.equals("desc")) {
            return productRepository.findEnabledByKeyword(keyword, PageRequest.of(page, PAGE_SIZE, Sort.by(sortBy).descending()));
        } else {
            return productRepository.findEnabledByKeyword(keyword, PageRequest.of(page, PAGE_SIZE, Sort.by(sortBy).ascending()));
        }
    }

}
