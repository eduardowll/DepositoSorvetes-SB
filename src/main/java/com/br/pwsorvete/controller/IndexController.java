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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

@Controller
public class IndexController {

    @Autowired
    private SorveteRepository sorveteRepository;

    @GetMapping({"/", "/index"})
    public String sorvete(Model model, HttpSession session) {
        List<Sorvete> sorvetes = sorveteRepository.findByIsDeletedIsNull();
        model.addAttribute("telaPrincipal", true);
        model.addAttribute("sorvetes", sorvetes);

        List<Sorvete> carrinho = (List<Sorvete>) session.getAttribute("carrinho");
        int quantidadeCarrinho = carrinho != null ? carrinho.size() : 0;
        model.addAttribute("quantidadeCarrinho", quantidadeCarrinho);

        return "index";
    }

    @GetMapping("/adicionarCarrinho")
    public String adicionarCarrinho(@RequestParam("id") Long id, HttpSession session, RedirectAttributes redirectAttributes) {
        List<Sorvete> carrinho = (List<Sorvete>) session.getAttribute("carrinho");
        if (carrinho == null) {
            carrinho = new ArrayList<>();
        }

        List<Sorvete> finalCarrinho = carrinho;
        sorveteRepository.findById(id).ifPresent(sorvete -> {
            finalCarrinho.add(sorvete);
            redirectAttributes.addFlashAttribute("mensagem", "Sorvete adicionado ao carrinho!");
        });

        session.setAttribute("carrinho", carrinho);
        return "redirect:/index";
    }

    @GetMapping("/verCarrinho")
    public String verCarrinho(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        List<Sorvete> carrinho = (List<Sorvete>) session.getAttribute("carrinho");

        if (carrinho == null || carrinho.isEmpty()) {
            redirectAttributes.addFlashAttribute("mensagem", "Não existem itens no carrinho");
            return "redirect:/index";
        }

        model.addAttribute("carrinho", carrinho);
        model.addAttribute("totalItens", carrinho.size());

        double total = carrinho.stream()
                .mapToDouble(Sorvete::getPreco)
                .sum();
        model.addAttribute("total", total);

        return "verCarrinho";
    }

    @GetMapping("/finalizarCompra")
    public String finalizarCompra(HttpSession session, RedirectAttributes redirectAttributes) {
        session.invalidate();

        redirectAttributes.addFlashAttribute("mensagem", "Compra finalizada com sucesso!");
        return "redirect:/index";
    }

    @GetMapping("/admin")
    public String admin(Model model) {
        List<Sorvete> todosSorvetes = sorveteRepository.findAll();
        model.addAttribute("sorvetes", todosSorvetes);
        return "admin";
    }

    @GetMapping("/cadastro")
    public String cadastro(Model model) {
        model.addAttribute("sorvete", new Sorvete());
        return "cadastro";
    }

    @PostMapping("/salvar")
    public String salvar(@Valid @ModelAttribute Sorvete sorvete, BindingResult result, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            if (sorvete.getId() != null) {
                return "editar";
            } else {
                return "cadastro";
            }
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

        if (sorvete.getId() != null) {
            redirectAttributes.addFlashAttribute("mensagem", "Sorvete cadastrado com sucesso!");
        } else {
            redirectAttributes.addFlashAttribute("mensagem", "Sorvete atualizado com sucesso!");
        }

        return "redirect:/admin";
    }

    @GetMapping("/editar")
    public String edicao(@RequestParam("id") Long id, Model model) {
        Sorvete sorvete = sorveteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sorvete não encontrado"));

        model.addAttribute("sorvete", sorvete);
        model.addAttribute("edicao", true);
        return "editar"; // Retorna a página de edição
    }

    @GetMapping("/deletar")
    public String deletarSorvete(@RequestParam("id") Long id, RedirectAttributes redirectAttributes) {
        Sorvete sorvete = sorveteRepository.findById(id).get();
        sorvete.setIsDeleted(LocalDate.now());
        sorveteRepository.save(sorvete);
        redirectAttributes.addFlashAttribute("mensagem", "Sorvete deletado com sucesso!");
        return "redirect:/admin";
    }

    @GetMapping("/restaurar")
    public String restaurarSorvete(@RequestParam("id") Long id, RedirectAttributes redirectAttributes) {
        Sorvete sorvete = sorveteRepository.findById(id).get();
        sorvete.setIsDeleted(null); // Remove a data (volta a null)
        sorveteRepository.save(sorvete);
        redirectAttributes.addFlashAttribute("mensagem", "Sorvete restaurado com sucesso!");
        return "redirect:/admin";
    }
}

