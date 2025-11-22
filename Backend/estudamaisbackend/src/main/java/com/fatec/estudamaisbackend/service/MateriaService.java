package com.fatec.estudamaisbackend.service;

import com.fatec.estudamaisbackend.entity.Materia;
import com.fatec.estudamaisbackend.repository.MateriaRepository;
import com.fatec.estudamaisbackend.service.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class MateriaService {

    private final MateriaRepository materiaRepository;

    public MateriaService(MateriaRepository materiaRepository) {
        this.materiaRepository = materiaRepository;
    }

    public List<Materia> findAll() {
        return materiaRepository.findAll();
    }

    public Materia findById(Long id) {
        return materiaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Matéria não encontrada com id: " + id));
    }

    public Materia create(Materia materia) {
        return materiaRepository.save(materia);
    }

    public Materia update(Long id, Materia incoming) {
        Materia existing = findById(id);
        existing.setNome(incoming.getNome());
        existing.setDescricao(incoming.getDescricao());
        existing.setIcone(incoming.getIcone());
        return materiaRepository.save(existing);
    }

    public void delete(Long id) {
        if (!materiaRepository.existsById(id)) {
            throw new ResourceNotFoundException("Matéria não encontrada com id: " + id);
        }
        materiaRepository.deleteById(id);
    }
}