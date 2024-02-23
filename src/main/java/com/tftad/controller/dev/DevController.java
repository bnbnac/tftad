package com.tftad.controller.dev;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

// controller only for develop --- 이런거 빌드할때 어떻게 구별?
@Controller
public class DevController {
    @GetMapping("/home")
    public String home() {
        return "home";
    }
}
