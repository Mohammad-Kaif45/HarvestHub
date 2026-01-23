package com.harvesthub.app.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

    @GetMapping("/login")
    public String showLoginPage() {
        return "login";
    }


    // @GetMapping("/farmer/dashboard") ...

    // --- DELETE THIS NOW ---
    // @GetMapping("/retail/home")
    // public String showRetailHome() {
    //    return "retail/retail_home";
    // }
    // -----------------------

//    @GetMapping("/wholesale/home")
//    public String showWholesaleHome() {
//        return "wholesale/wholesale_home";
//    }
}