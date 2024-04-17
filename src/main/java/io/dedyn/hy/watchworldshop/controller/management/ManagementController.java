package io.dedyn.hy.watchworldshop.controller.management;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/management")
public class ManagementController {
    @RequestMapping("")
    public String index() {
        return "management/index";
    }
}
