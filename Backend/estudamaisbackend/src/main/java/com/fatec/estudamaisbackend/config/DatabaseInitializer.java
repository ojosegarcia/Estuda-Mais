package com.fatec.estudamaisbackend.config;

import com.fatec.estudamaisbackend.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class DatabaseInitializer implements CommandLineRunner {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        System.out.println("🔧 Inicializando banco de dados...");
        
        // Atualiza TODOS os professores para aprovado=true
        int professoresAtualizados = usuarioRepository.aprovarTodosProfessores();
        
        if (professoresAtualizados > 0) {
            System.out.println("✅ " + professoresAtualizados + " professores foram aprovados automaticamente!");
        } else {
            System.out.println("✅ Todos os professores já estão aprovados.");
        }
    }
}
