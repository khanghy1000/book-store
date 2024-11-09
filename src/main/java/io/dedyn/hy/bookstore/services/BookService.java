package io.dedyn.hy.bookstore.services;

import io.dedyn.hy.bookstore.entities.book.Book;
import io.dedyn.hy.bookstore.repositories.BookRepository;
import io.dedyn.hy.bookstore.utils.SlugifyUtil;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class BookService {
    private final BookRepository bookRepository;

    private final int PAGE_SIZE = 10;

    @Autowired
    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public List<Book> findAll() {
        return bookRepository.findAll();
    }

    public List<Book> findAll(Sort sort) {
        return bookRepository.findAll(sort);
    }

    public Book findById(Long id) {
        return bookRepository.findById(id).orElse(null);
    }

    public Book findBySlug(String slug) {
        return bookRepository.findFirstBySlug(slug);
    }

    public Long count() {
        return bookRepository.count();
    }

    public Book save(Book book) {
        book.setSlug(SlugifyUtil.slugify(book.getName()));
        return bookRepository.save(book);
    }

    public void deleteById(Long id) {
        bookRepository.deleteById(id);
    }

    public boolean isUniqueName(Book book) {
        Book dbBook = bookRepository.findFirstBySlug(SlugifyUtil.slugify((book.getName())));
        if (dbBook == null) return true;
        if (book.getId() == null) return false;
        return book.getId().equals(dbBook.getId());
    }

    public List<Book> findByKeyword(String keyword) {
        return bookRepository.findByKeyword(keyword);
    }

    public Page<Book> findByKeyword(String keyword, Integer page) {
        if (page > 0) page--;
        if (page < 0) page = 0;

        return bookRepository.findByKeyword(keyword, PageRequest.of(page, PAGE_SIZE, Sort.by("name").ascending()));
    }

    public Page<Book> findEnabledByKeyword(String keyword,
                                           Integer publisherId,
                                           Integer categoryId,
                                           Integer page,
                                           String sortBy,
                                           String order) {
        if (page > 0) page--;
        if (page < 0) page = 0;

        if (publisherId != null && publisherId != 0 && categoryId != null && categoryId != 0) {
            if (order.equals("desc")) {
                return bookRepository.findEnabledByKeywordAndPublisherAndCategory(keyword, publisherId, categoryId, PageRequest.of(page, PAGE_SIZE, Sort.by(sortBy).descending()));
            } else {
                return bookRepository.findEnabledByKeywordAndPublisherAndCategory(keyword, publisherId, categoryId, PageRequest.of(page, PAGE_SIZE, Sort.by(sortBy).ascending()));
            }
        }

        if (publisherId != null && publisherId != 0) {
            if (order.equals("desc")) {
                return bookRepository.findEnabledByKeywordAndPublisher(keyword, publisherId, PageRequest.of(page, PAGE_SIZE, Sort.by(sortBy).descending()));
            } else {
                return bookRepository.findEnabledByKeywordAndPublisher(keyword, publisherId, PageRequest.of(page, PAGE_SIZE, Sort.by(sortBy).ascending()));
            }
        }

        if (categoryId != null && categoryId != 0) {
            if (order.equals("desc")) {
                return bookRepository.findEnabledByKeywordAndCategory(keyword, categoryId, PageRequest.of(page, PAGE_SIZE, Sort.by(sortBy).descending()));
            } else {
                return bookRepository.findEnabledByKeywordAndCategory(keyword, categoryId, PageRequest.of(page, PAGE_SIZE, Sort.by(sortBy).ascending()));
            }
        }

        if (order.equals("desc")) {
            return bookRepository.findEnabledByKeyword(keyword, PageRequest.of(page, PAGE_SIZE, Sort.by(sortBy).descending()));
        } else {
            return bookRepository.findEnabledByKeyword(keyword, PageRequest.of(page, PAGE_SIZE, Sort.by(sortBy).ascending()));
        }
    }

}
