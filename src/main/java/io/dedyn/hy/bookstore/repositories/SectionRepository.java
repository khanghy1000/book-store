package io.dedyn.hy.bookstore.repositories;

import io.dedyn.hy.bookstore.entities.section.Section;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SectionRepository extends JpaRepository<Section, Long> {
    @Query("SELECT s FROM Section s ORDER BY s.order ASC")
    public List<Section> findALLOrderByOrderAsc();

    @Query("SELECT s FROM Section s WHERE s.enabled = TRUE ORDER BY s.order ASC")
    public List<Section> findAllEnabledOrderByOrderAsc();

    @Modifying
    @Query("UPDATE Section s SET s.enabled = ?2 WHERE s.id = ?1")
    public void updateEnabledStatus(Long id, boolean enabled);
}