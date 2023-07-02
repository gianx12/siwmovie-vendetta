package it.uniroma3.siw.controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ErrController implements ErrorController {
    @GetMapping("/error")
    public String error(HttpServletRequest request, Model model){
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        if(status != null){
            model.addAttribute("statuscode", Integer.valueOf(status.toString()));
            return "/error/error.html";
        }
        return "/error/error.html";
    }
}
