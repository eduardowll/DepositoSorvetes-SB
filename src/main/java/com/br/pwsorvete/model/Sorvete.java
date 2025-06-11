package com.br.pwsorvete.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDate;

@Entity
public class Sorvete {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate isDeleted;

    @NotBlank(message = "O nome do sorvete é obrigatório")
    private String nome;

    @NotBlank(message = "O sabor é obrigatório")
    private String sabor;

    @NotNull(message = "A quantidade é obrigatória")
    @Min(value = 1, message = "A quantidade deve ser maior que 0")
    private Integer quantidade;

    @NotNull(message = "O preço é obrigatório")
    @DecimalMin(value = "0.0", inclusive = false, message = "O preço deve ser maior que 0")
    private Double preco;

    @NotBlank(message = "A marca é obrigatória")
    private String marca;

    private String imageUrl;

    public Sorvete() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public LocalDate getIsDeleted() { return isDeleted; }
    public void setIsDeleted(LocalDate isDeleted) { this.isDeleted = isDeleted; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getSabor() { return sabor; }
    public void setSabor(String sabor) { this.sabor = sabor; }

    public Integer getQuantidade() { return quantidade; }
    public void setQuantidade(Integer quantidade) { this.quantidade = quantidade; }

    public Double getPreco() { return preco; }
    public void setPreco(Double preco) { this.preco = preco; }

    public String getMarca() { return marca; }
    public void setMarca(String marca) { this.marca = marca; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
}
