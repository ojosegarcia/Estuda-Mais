package com.fatec.estudamaisbackend.service;

import com.fatec.estudamaisbackend.entity.Materia;
import com.fatec.estudamaisbackend.entity.Professor;
import com.fatec.estudamaisbackend.repository.MateriaRepository;
import com.fatec.estudamaisbackend.repository.ProfessorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ProfessorService {

    @Autowired
    private ProfessorRepository professorRepository;

    @Autowired
    private MateriaRepository materiaRepository;

    public List<Professor> findAll() {
        List<Professor> professores = professorRepository.findAll();
        System.out.println("📋 ProfessorService.findAll() - Total: " + professores.size());
        for (Professor p : professores) {
            System.out.println("  👤 Professor ID=" + p.getId() + ", Nome=" + p.getNomeCompleto() + 
                             ", Aprovado=" + p.getAprovado() + ", Matérias=" + p.getMaterias().size());
            if (!p.getMaterias().isEmpty()) {
                for (Materia m : p.getMaterias()) {
                    System.out.println("     - Matéria: ID=" + m.getId() + ", Nome=" + m.getNome());
                }
            }
        }
        return professores;
    }

    public Professor findById(Long id) {
        return professorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Professor não encontrado com ID: " + id));
    }

    @Transactional
    public Professor create(Professor professor) {
        if (professor.getDataCadastro() == null)
            professor.setDataCadastro(LocalDateTime.now());
        if (professor.getAtivo() == null)
            professor.setAtivo(true);
        // 🔥 SEMPRE APROVADO: Força true independente do valor recebido
        professor.setAprovado(true);
        return professorRepository.save(professor);
    }

    @Transactional
    public Professor createComMaterias(Professor professor, List<Long> idsMaterias) {
        System.out.println("🔥 ProfessorService.createComMaterias() - IDs recebidos: " + idsMaterias);
        
        if (professor.getDataCadastro() == null)
            professor.setDataCadastro(LocalDateTime.now());
        if (professor.getAtivo() == null)
            professor.setAtivo(true);
        // 🔥 SEMPRE APROVADO: Força true independente do valor recebido
        professor.setAprovado(true);
        
        // 🔥 CRÍTICO: Vincular matérias ANTES de salvar
        if (idsMaterias != null && !idsMaterias.isEmpty()) {
            List<Materia> materias = materiaRepository.findAllById(idsMaterias);
            System.out.println("📚 Matérias carregadas: " + materias.size() + " items");
            professor.getMaterias().clear();
            professor.getMaterias().addAll(materias);
        }
        
        Professor saved = professorRepository.save(professor);
        System.out.println("✅ Professor salvo com ID: " + saved.getId() + ", Matérias: " + saved.getMaterias().size());
        return saved;
    }

    // Este método é usado apenas para updates simples ou internos
    @Transactional
    public Professor update(Long id, Professor dadosAtualizados) {
        return updateComMaterias(id, dadosAtualizados, null);
    }

    @Transactional
    public Professor updateComMaterias(Long id, Professor dadosAtualizados, List<Long> idsMaterias) {
        System.out.println("🔥 ProfessorService.updateComMaterias() - ID: " + id + ", IDs recebidos: " + idsMaterias);
        
        Professor professorExistente = findById(id);
        System.out.println("📖 Professor carregado - ID: " + professorExistente.getId() + ", Matérias atuais: " + professorExistente.getMaterias().size());
        
        if (dadosAtualizados.getNomeCompleto() != null)
            professorExistente.setNomeCompleto(dadosAtualizados.getNomeCompleto());
        if (dadosAtualizados.getTelefone() != null)
            professorExistente.setTelefone(dadosAtualizados.getTelefone());
        if (dadosAtualizados.getFotoPerfil() != null)
            professorExistente.setFotoPerfil(dadosAtualizados.getFotoPerfil());
        if (dadosAtualizados.getSobre() != null)
            professorExistente.setSobre(dadosAtualizados.getSobre());
        if (dadosAtualizados.getMetodologia() != null)
            professorExistente.setMetodologia(dadosAtualizados.getMetodologia());
        if (dadosAtualizados.getValorHora() != null)
            professorExistente.setValorHora(dadosAtualizados.getValorHora());
        if (dadosAtualizados.getLinkPadraoAula() != null)
            professorExistente.setLinkPadraoAula(dadosAtualizados.getLinkPadraoAula());
        if (dadosAtualizados.getUsarLinkPadrao() != null)
            professorExistente.setUsarLinkPadrao(dadosAtualizados.getUsarLinkPadrao());


        if (idsMaterias != null) {
            System.out.println("🧹 Limpando matérias antigas...");
            professorExistente.getMaterias().clear();
            
            if (!idsMaterias.isEmpty()) {
                System.out.println("🔍 Buscando matérias com IDs: " + idsMaterias);
                List<Materia> novasMaterias = materiaRepository.findAllById(idsMaterias);
                System.out.println("📚 Matérias encontradas: " + novasMaterias.size());
                
                if (!novasMaterias.isEmpty()) {
                    for (Materia m : novasMaterias) {
                        System.out.println("  - Matéria: ID=" + m.getId() + ", Nome=" + m.getNome());
                    }
                }
                
                professorExistente.getMaterias().addAll(novasMaterias);
                System.out.println("✅ Matérias adicionadas. Total agora: " + professorExistente.getMaterias().size());
            }
        } else {
            System.out.println("⚠️ idsMaterias é NULL - matérias não serão alteradas");
        }
        
        Professor saved = professorRepository.save(professorExistente);
        System.out.println("💾 Professor salvo! ID: " + saved.getId() + ", Matérias finais: " + saved.getMaterias().size());
        
        return saved;
    }

    

    public void delete(Long id) {
        Professor professor = findById(id);
        professorRepository.delete(professor);
    }
}