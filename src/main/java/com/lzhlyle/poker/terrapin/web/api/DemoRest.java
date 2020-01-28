package com.lzhlyle.poker.terrapin.web.api;

import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/demo")
public class DemoRest {

    /**
     * demo rest
     *
     * @param id
     * @return
     */
    @ApiOperation(value = "get demo")
    @RequestMapping(value = "/get/{id}", method = RequestMethod.GET)
    public String get(@PathVariable(name = "id") String id, HttpServletRequest request) {
        return "response from server: " + id;
    }
}
