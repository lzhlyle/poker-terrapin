package com.lzhlyle.poker.terrapin.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/demo")
public class DemoController {

    @RequestMapping(value = {"/", "/index"}, method = RequestMethod.GET)
    public Object index(Model model, HttpServletRequest request) {
        return "demo/index";
    }

    @RequestMapping(path = "/ws", method = RequestMethod.GET)
    public String ws(Model model, HttpServletRequest request) {
        return "demo/ws";
    }
}
