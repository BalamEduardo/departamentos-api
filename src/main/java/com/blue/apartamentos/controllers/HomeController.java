package com.blue.apartamentos.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    @GetMapping("/")
    public String home() {
        return "redirect:/propiedades.html";
    }
    @GetMapping({"/clientes", "/clientes/new", "/clientes/nuevo", "/clientes/{id}/edit", "/clientes/{id}/editar"})
    public String clientes() { return "redirect:/clientes.html"; }
}

