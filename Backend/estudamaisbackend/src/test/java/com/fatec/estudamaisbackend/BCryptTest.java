package com.fatec.estudamaisbackend;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

public class BCryptTest {
    
    @Test
    public void testBCrypt() {
        PasswordEncoder encoder = new BCryptPasswordEncoder();
        
        // Hash existente no banco
        String hashExistente = "$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy";
        String senha = "123456";
        
        System.out.println("=== TESTE BCRYPT ===");
        System.out.println("Senha plain: " + senha);
        System.out.println("Hash existente: " + hashExistente);
        System.out.println("Matches: " + encoder.matches(senha, hashExistente));
        
        // Gerar novo hash
        String novoHash = encoder.encode(senha);
        System.out.println("\nNovo hash gerado: " + novoHash);
        System.out.println("Novo hash matches: " + encoder.matches(senha, novoHash));
    }
}
