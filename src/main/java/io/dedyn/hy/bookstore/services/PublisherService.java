package io.dedyn.hy.bookstore.services;

import io.dedyn.hy.bookstore.entities.Publisher;
import io.dedyn.hy.bookstore.repositories.PublisherRepository;
import io.dedyn.hy.bookstore.utils.SlugifyUtil;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class PublisherService {
    private final PublisherRepository publisherRepository;

    @Autowired
    public PublisherService(PublisherRepository publisherRepository) {
        this.publisherRepository = publisherRepository;
    }


    public List<Publisher> findAll() {
        return publisherRepository.findAll(Sort.by("name"));
    }

    public Publisher findById(Integer id) {
        return publisherRepository.findById(id).orElse(null);
    }

    public Publisher save(Publisher publisher) {
        publisher.setSlug(SlugifyUtil.slugify(publisher.getName()));
        return publisherRepository.save(publisher);
    }

    public void deleteById(Integer id) {
        publisherRepository.deleteById(id);
    }

    public boolean isUniqueName(Publisher publisher) {
        Publisher dbPublisher = publisherRepository.findFirstBySlug(SlugifyUtil.slugify(publisher.getName()));
        if (dbPublisher == null) return true;
        if (publisher.getId() == null) return false;
        return publisher.getId().equals(dbPublisher.getId());
    }
}
