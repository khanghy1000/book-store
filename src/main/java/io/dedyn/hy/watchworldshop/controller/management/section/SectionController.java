package io.dedyn.hy.watchworldshop.controller.management.section;

import io.dedyn.hy.watchworldshop.entities.section.Section;
import io.dedyn.hy.watchworldshop.exception.SectionNotFoundException;
import io.dedyn.hy.watchworldshop.exception.SectionUnmoveableException;
import io.dedyn.hy.watchworldshop.services.SectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/management/sections")
public class SectionController {

    private final SectionService sectionService;

    @Autowired
    public SectionController(SectionService sectionService) {
        this.sectionService = sectionService;
    }

    @GetMapping("")
    public String index(Model model) {
        List<Section> sections = sectionService.findAll();
        model.addAttribute("sections", sections);

        return "management/sections/index";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable(name = "id") Long id,
                         RedirectAttributes redirectAttributes) {
        sectionService.deleteById(id);
        redirectAttributes.addFlashAttribute("message", "Mục có id " + id + " đã được xóa.");

        return "redirect:/management/sections";
    }

    @PostMapping("/enable/{id}/{enabled}")
    public String updateEnabledStatus(@PathVariable(name = "id") Long id,
                                      @PathVariable("enabled") Boolean enabledStatus,
                                      RedirectAttributes redirectAttributes) {
        sectionService.updateEnabledStatus(id, enabledStatus);
        String updateResult = enabledStatus ? "hiển thị." : "ẩn.";
        redirectAttributes.addFlashAttribute("message", "Mục có id " + id + " đã được " + updateResult);

        return "redirect:/management/sections";
    }

    @PostMapping("/moveup/{id}")
    public String moveSectionUp(@PathVariable(name = "id") Long id, RedirectAttributes redirectAttributes) {
        try {
            sectionService.moveUp(id);
            redirectAttributes.addFlashAttribute("message", "Mục có id " + id + " đã được di chuyển lên một vị trí.");
        } catch (SectionUnmoveableException | SectionNotFoundException ex) {
            redirectAttributes.addFlashAttribute("message", ex.getMessage());
        }

        return "redirect:/management/sections";
    }

    @PostMapping("/movedown/{id}")
    public String moveSectionDown(@PathVariable(name = "id") Long id, RedirectAttributes redirectAttributes) {
        try {
            sectionService.moveDown(id);
            redirectAttributes.addFlashAttribute("message", "Mục có id " + id + " đã được di chuyển xuống một vị trí.");
        } catch (SectionUnmoveableException | SectionNotFoundException ex) {
            redirectAttributes.addFlashAttribute("message", ex.getMessage());
        }

        return "redirect:/management/sections";
    }

    @ModelAttribute("currentTab")
    public String currentTab() {
        return "sections";
    }
}
