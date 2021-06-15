package com.bitsvaley.micro.controllers;

import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

@Controller("error")
public class ErrorController {

    @ExceptionHandler(Exception.class)
    public ModelAndView handleException(@NotNull HttpServletRequest request, Exception ex) {
        ex.printStackTrace();
        ModelAndView mv = new ModelAndView();

        mv.addObject("exception", ex.getLocalizedMessage());
        mv.addObject("url", request.getRequestURL());

        mv.setViewName("error");
        return mv;
    }
}
