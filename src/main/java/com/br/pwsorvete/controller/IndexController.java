package com.br.pwsorvete.controller;

import com.br.pwsorvete.model.Sorvete;
import com.br.pwsorvete.repository.SorveteRepository;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.validation.BindingResult;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

@Controller
public class IndexController {

    @Autowired
    private SorveteRepository sorveteRepository;

    // Página inicial com lista de sorvetes
    @GetMapping({"/", "/index"})
    public String sorvete(Model model) {
        List<Sorvete> sorvetes = sorveteRepository.findByIsDeletedIsNull();
        model.addAttribute("telaPrincipal", true);
        model.addAttribute("sorvetes", sorvetes);
        return "index";
    }

    // Adiciona um sorvete ao carrinho
    @GetMapping("/adicionarCarrinho")
    public String adicionarCarrinho(@RequestParam("id") Long id, HttpSession session) {
        List<Sorvete> carrinho = (List<Sorvete>) session.getAttribute("carrinho");
        if (carrinho == null) {
            carrinho = new ArrayList<>();
        }

        sorveteRepository.findById(id).ifPresent(carrinho::add);
        session.setAttribute("carrinho", carrinho);

        return "redirect:/index";
    }

    @GetMapping("/admin")
    public String admin(Model model) {
        List<Sorvete> todosSorvetes = sorveteRepository.findAll(); // inclusive deletados
        model.addAttribute("sorvetes", todosSorvetes);
        return "admin";
    }

    @GetMapping("/cadastro")
    public String mostrarFormularioCadastro(Model model) {
        model.addAttribute("sorvete", new Sorvete());
        return "cadastro";
    }

    @PostMapping("/salvar")
    public String salvarSorvete(@Valid @ModelAttribute Sorvete sorvete, BindingResult result) {
        if (result.hasErrors()) {
            return "cadastro";
        }
        List<String> imagens = Arrays.asList(
                "/images/img.png",
                "/images/img1.png",
                "/images/img2.png"
        );
        Random random = new Random();
        String imgSorteada = imagens.get(random.nextInt(imagens.size()));
        sorvete.setImageUrl(imgSorteada);

        sorvete.setIsDeleted(null);
        sorveteRepository.save(sorvete);
        return "redirect:/admin";
    }
}

