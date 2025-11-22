package com.fatec.estudamaisbackend.service;

import com.fatec.estudamaisbackend.entity.Materia;
import com.fatec.estudamaisbackend.entity.Professor;
import com.fatec.estudamaisbackend.repository.MateriaRepository;
import com.fatec.estudamaisbackend.repository.ProfessorRepository;
import com.fatec.estudamaisbackend.repository.UsuarioRepository;
import com.fatec.estudamaisbackend.service.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ProfessorService {

    private final ProfessorRepository professorRepository;
    private final UsuarioRepository usuarioRepository;
    private final MateriaRepository materiaRepository;

    public ProfessorService(ProfessorRepository professorRepository,
                            UsuarioRepository usuarioRepository,
                            MateriaRepository materiaRepository) {
        this.professorRepository = professorRepository;
        this.usuarioRepository = usuarioRepository;
        this.materiaRepository = materiaRepository;
    }

    public List<Professor> findAll() {
        return professorRepository.findAll();
    }

    public Professor findById(Long id) {
        return professorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Professor não encontrado com id: " + id));
    }

    public Professor create(Professor professor) {
        return professorRepository.save(professor);
    }

    public Professor update(Long id, Professor incoming) {
        Professor existing = findById(id);
        existing.setSobre(incoming.getSobre());
        existing.setMetodologia(incoming.getMetodologia());
        existing.setValorHora(incoming.getValorHora());
        existing.setImagemCertificado(incoming.getImagemCertificado());
        existing.setAprovado(incoming.getAprovado());

        if (incoming.getMaterias() != null) {
            existing.getMaterias().clear();
            for (Materia m : incoming.getMaterias()) {
                materiaRepository.findById(m.getId()).ifPresent(existing.getMaterias()::add);
            }
        }

        return professorRepository.save(existing);
    }

    public void delete(Long id) {
        if (!professorRepository.existsById(id)) {
            throw new ResourceNotFoundException("Professor não encontrado com id: " + id);
        }
        professorRepository.deleteById(id);
    }
}