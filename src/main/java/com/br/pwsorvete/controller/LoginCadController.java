package com.br.pwsorvete.controller;

import com.br.pwsorvete.model.Usuario;
import com.br.pwsorvete.repository.UsuarioRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
public class LoginCadController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/cadastroUsuario")
    public String cadastroUsuario(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "cadastroUsuario";
    }

    @PostMapping("/salvarUsuario")
    public String salvarUsuario(@Valid @ModelAttribute("usuario") Usuario usuario, BindingResult result, Model model) {

        if (result.hasErrors()) {
            model.addAttribute("usuario", usuario);
            return "cadastroUsuario";
        }

        if (usuarioRepository.findByUsername(usuario.getUsername()).isPresent()) {
            model.addAttribute("erro", "Nome de usuário já cadastrado.");
            model.addAttribute("usuario", usuario);
            return "cadastroUsuario";
        }

        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        usuario.setAtivo(true);
        usuarioRepository.save(usuario);

        return "redirect:/login?cadastro=sucesso";
    }
}