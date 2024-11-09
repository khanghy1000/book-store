package io.dedyn.hy.bookstore.controller.management.section;

import io.dedyn.hy.bookstore.entities.Publisher;
import io.dedyn.hy.bookstore.entities.section.Section;
import io.dedyn.hy.bookstore.entities.section.SectionPublisher;
import io.dedyn.hy.bookstore.entities.section.SectionType;
import io.dedyn.hy.bookstore.services.PublisherService;
import io.dedyn.hy.bookstore.services.SectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/management/sections/publisher")
public class PublisherSectionController {

    private final SectionService sectionService;
    private final PublisherService publisherService;

    @Autowired
    public PublisherSectionController(SectionService sectionService, PublisherService publisherService) {
        this.sectionService = sectionService;
        this.publisherService = publisherService;
    }

    @GetMapping("/create")
    public String create(Model model) {
        Section section = Section.builder().enabled(true).type(SectionType.PUBLISHER).build();
        List<Publisher> publishers = publisherService.findAll();

        model.addAttribute("publishers", publishers);
        model.addAttribute("section", section);

        return "management/sections/publisher_section_form";
    }

    @PostMapping("/create")
    public String create(Section section,
                         @RequestParam(name = "selectedPublishers", required = false) String[] selectedPublishers,
                         RedirectAttributes redirectAttributes) {
        section.setId(null);
        if (selectedPublishers != null && selectedPublishers.length > 0) {
            for (int i = 0; i < selectedPublishers.length; i++) {
                Integer publisherId = Integer.parseInt(selectedPublishers[i].split("-")[0]);
//                Long sectionPublisherId = Long.parseLong(selectedPublishers[i].split("-")[1]);

                SectionPublisher sectionPublisher = new SectionPublisher();
                sectionPublisher.setSection(section);
                sectionPublisher.setPublisher(Publisher.builder().id(publisherId).build());
                sectionPublisher.setOrder(i);
                section.addSectionPublisher(sectionPublisher);
            }
        }
        sectionService.save(section);

        redirectAttributes.addFlashAttribute("message", "Tạo mục mới thành công.");
        return "redirect:/management/sections";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable(name = "id") Long id,
                       Model model) {
        Section section = sectionService.findById(id);
        List<Publisher> listPublishers = publisherService.findAll();

        model.addAttribute("publishers", listPublishers);
        model.addAttribute("section", section);

        return "management/sections/publisher_section_form";

    }

    @PostMapping("/edit")
    public String edit(Section section,
                       @RequestParam(name = "selectedPublishers", required = false) String[] selectedPublishers,
                       RedirectAttributes redirectAttributes) {

        if (selectedPublishers != null && selectedPublishers.length > 0) {
            for (int i = 0; i < selectedPublishers.length; i++) {
                Integer publisherId = Integer.parseInt(selectedPublishers[i].split("-")[0]);
                long sectionPublisherId = Long.parseLong(selectedPublishers[i].split("-")[1]);

                SectionPublisher sectionPublisher = new SectionPublisher();
                sectionPublisher.setId(sectionPublisherId == 0 ? null : sectionPublisherId);
                sectionPublisher.setSection(section);
                sectionPublisher.setPublisher(Publisher.builder().id(publisherId).build());
                sectionPublisher.setOrder(i);
                section.addSectionPublisher(sectionPublisher);
            }
        }
        sectionService.save(section);

        redirectAttributes.addFlashAttribute("message", "Cập nhật mục thành công.");
        return "redirect:/management/sections";
    }

    @ModelAttribute("currentTab")
    public String currentTab() {
        return "sections";
    }
}

