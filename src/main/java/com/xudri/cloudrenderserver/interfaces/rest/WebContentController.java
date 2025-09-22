package com.xudri.cloudrenderserver.interfaces.rest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebContentController {

    @GetMapping("/player/")
    public String playerView() {
        return "player/index";
    }

    @GetMapping("")
    public String indexView() {
        return "index";
    }

    @GetMapping("login")
    public String loginView() {
        return "index";
    }

}
