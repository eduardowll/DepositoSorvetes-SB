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

    // Página inicial com lista de sorvetes
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

    // Adiciona um sorvete ao carrinho
    @GetMapping("/adicionarCarrinho")
    public String adicionarCarrinho(@RequestParam("id") Long id, HttpSession session, RedirectAttributes redirectAttributes) {
        List<Sorvete> carrinho = (List<Sorvete>) session.getAttribute("carrinho");
        if (carrinho == null) {
            carrinho = new ArrayList<>();
        }

        // Busca o sorvete no banco e adiciona ao carrinho
        List<Sorvete> finalCarrinho = carrinho;
        sorveteRepository.findById(id).ifPresent(sorvete -> {
            finalCarrinho.add(sorvete);
            redirectAttributes.addFlashAttribute("mensagem", "Sorvete adicionado ao carrinho!");
        });

        session.setAttribute("carrinho", carrinho);
        return "redirect:/index";
    }

    // QUESTÃO 10: Ver carrinho
    @GetMapping("/verCarrinho")
    public String verCarrinho(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        List<Sorvete> carrinho = (List<Sorvete>) session.getAttribute("carrinho");

        // Se carrinho estiver vazio, redireciona para index
        if (carrinho == null || carrinho.isEmpty()) {
            redirectAttributes.addFlashAttribute("mensagem", "Não existem itens no carrinho");
            return "redirect:/index";
        }

        model.addAttribute("carrinho", carrinho);
        model.addAttribute("totalItens", carrinho.size());

        // Calcula total (opcional)
        double total = carrinho.stream()
                .mapToDouble(Sorvete::getPreco)
                .sum();
        model.addAttribute("total", total);

        return "verCarrinho";
    }

    // QUESTÃO 11: Finalizar compra
    @GetMapping("/finalizarCompra")
    public String finalizarCompra(HttpSession session, RedirectAttributes redirectAttributes) {
        // Invalida a sessão existente
        session.invalidate();

        redirectAttributes.addFlashAttribute("mensagem", "Compra finalizada com sucesso!");
        return "redirect:/index";
    }

    @GetMapping("/admin")
    public String admin(Model model) {
        List<Sorvete> todosSorvetes = sorveteRepository.findAll(); // inclusive deletados
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
        model.addAttribute("edicao", true); // Flag para indicar que é edição
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

