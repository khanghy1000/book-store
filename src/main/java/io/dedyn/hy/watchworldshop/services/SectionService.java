package io.dedyn.hy.watchworldshop.services;

import io.dedyn.hy.watchworldshop.entities.section.Section;
import io.dedyn.hy.watchworldshop.exception.SectionNotFoundException;
import io.dedyn.hy.watchworldshop.exception.SectionUnmoveableException;
import io.dedyn.hy.watchworldshop.repositories.SectionRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class SectionService {

    private final SectionRepository sectionRepository;

    public SectionService(SectionRepository sectionRepository) {
        this.sectionRepository = sectionRepository;
    }

    public List<Section> findAll() {
        return sectionRepository.findALLOrderByOrderAsc();
    }

    public List<Section> findAllEnabled() {
        return sectionRepository.findAllEnabledOrderByOrderAsc();
    }

    public void save(Section section) {
        if (section.getId() == null) {
            long newPosition = sectionRepository.count() + 1;
            section.setOrder((int) newPosition);
        }
        sectionRepository.save(section);
    }

    public Section findById(Long id) {
        return sectionRepository.findById(id).orElse(null);
    }

    public void deleteById(Long id) {
        sectionRepository.deleteById(id);
        refreshOrder();
    }

    public void refreshOrder() {
        List<Section> sections = sectionRepository.findALLOrderByOrderAsc();
        for (int i = 0; i < sections.size(); i++) {
            Section section = sections.get(i);
            section.setOrder(i + 1);
        }
        sectionRepository.saveAll(sections);
    }

    public void updateEnabledStatus(Long id, boolean enabled) {
        sectionRepository.updateEnabledStatus(id, enabled);
    }

    public void moveUp(Long id) throws SectionNotFoundException, SectionUnmoveableException {
        Section currentSection = sectionRepository.findById(id).orElse(null);
        if (currentSection == null) {
            throw new SectionNotFoundException("Không tìm được mục có id " + id);
        }

        List<Section> sections = sectionRepository.findALLOrderByOrderAsc();

        int currentSectionIndex = sections.indexOf(currentSection);
        if (currentSectionIndex == 0) {
            throw new SectionUnmoveableException("Mục có id " + id + " đã ở vị trí đầu tiên");
        }

        int previousSectionIndex = currentSectionIndex - 1;
        Section previousSection = sections.get(previousSectionIndex);

        currentSection.setOrder(previousSectionIndex + 1);
        previousSection.setOrder(currentSectionIndex + 1);

        sectionRepository.save(currentSection);
        sectionRepository.save(previousSection);
    }

    public void moveDown(Long id) throws SectionNotFoundException, SectionUnmoveableException {
        Section currentSection = sectionRepository.findById(id).orElse(null);
        if (currentSection == null) {
            throw new SectionNotFoundException("Không tìm được mục có id " + id);
        }

        List<Section> allSections = sectionRepository.findALLOrderByOrderAsc();

        int currentSectionIndex = allSections.indexOf(currentSection);
        if (currentSectionIndex == allSections.size() - 1) {
            throw new SectionUnmoveableException("Mục có id " + id + " đã ở vị trí cuối cùng");
        }

        int nextSectionIndex = currentSectionIndex + 1;
        Section nextSection = allSections.get(nextSectionIndex);

        currentSection.setOrder(nextSectionIndex + 1);
        nextSection.setOrder(currentSectionIndex + 1);

        sectionRepository.save(currentSection);
        sectionRepository.save(nextSection);
    }
}
