package com.fatec.estudamaisbackend.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "aluno")
@PrimaryKeyJoinColumn(name = "id_usuario")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@DiscriminatorValue("ALUNO")
public class Aluno extends Usuario {

    private String escolaridade;
    
    private String interesse;
    
    // Construtor para definir tipoUsuario manualmente se necessário
    @PrePersist
    public void prePersist() {
        // Lógica para definir dataCadastro se for nulo
        if (this.getDataCadastro() == null) {
            this.setDataCadastro(java.time.LocalDateTime.now());
        }
    }
}

    
