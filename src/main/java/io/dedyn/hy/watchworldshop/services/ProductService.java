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
}
