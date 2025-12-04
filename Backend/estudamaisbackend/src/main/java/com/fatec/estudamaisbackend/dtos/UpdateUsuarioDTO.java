package com.fatec.estudamaisbackend.dtos;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.LocalDate;
import java.util.List;

@Data
public class UpdateUsuarioDTO {
    // Campos comuns de Usuario
    @Size(min = 2, max = 255, message = "Nome completo deve ter entre 2 e 255 caracteres")
    @Pattern(regexp = "^[A-Za-zÀ-ÿ\\s]{2,}$", message = "Nome deve conter apenas letras e ter no mínimo 2 caracteres")
    private String nomeCompleto;
    
    @Pattern(regexp = "^\\(?\\d{2}\\)?\\s?9?\\d{4}-?\\d{4}$", 
             message = "Telefone inválido. Ex: (11) 98765-4321 ou 11987654321")
    private String telefone;
    
    @Past(message = "Data de nascimento deve ser no passado")
    private LocalDate dataNascimento;
    
    @Pattern(regexp = "^(MASCULINO|FEMININO|OUTRO|PREFIRO_NAO_DIZER)$", 
             message = "Sexo deve ser MASCULINO, FEMININO, OUTRO ou PREFIRO_NAO_DIZER")
    private String sexo;
    
    private String fotoPerfil; // Não obrigatório

    // ADICIONADO: campo opcional para alteração de senha via PUT /api/usuarios/{id}
    @Size(min = 6, max = 100, message = "Senha deve ter ao menos 6 caracteres")
    private String password;
    
    // Campos de Aluno
    private String escolaridade;
    private String interesse;
    
    // Campos de Professor
    @Size(min = 50, max = 2000, message = "Sobre mim deve ter entre 50 e 2000 caracteres")
    private String sobre;
    
    @Size(min = 30, max = 1000, message = "Metodologia deve ter entre 30 e 1000 caracteres")
    private String metodologia;
    
    @DecimalMin(value = "0.01", message = "Valor/hora deve ser maior que zero")
    @DecimalMax(value = "9999.99", message = "Valor/hora deve ser menor que R$ 10.000")
    private Double valorHora;
    
    private String fotoCertificado; // Não obrigatório
    private Boolean aprovado;
    private String linkPadraoAula;
    private Boolean usarLinkPadrao;
    
    // Lista de IDs de matérias (para Professor)
    private List<Long> materiaIds;
}