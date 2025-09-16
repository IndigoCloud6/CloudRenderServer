package com.xudri.cloudrenderserver.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebContentController {

    @GetMapping("/player")
    public String playerView() {
        return "player/index";
    }

    @GetMapping("/index")
    public String indexView() {
        return "index";
    }

}
