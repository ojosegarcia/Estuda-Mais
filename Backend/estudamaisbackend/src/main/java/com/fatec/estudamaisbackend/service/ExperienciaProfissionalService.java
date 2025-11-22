package com.fatec.estudamaisbackend.service;

import com.fatec.estudamaisbackend.dtos.ExperienciaProfissionalDTO;
import com.fatec.estudamaisbackend.entity.ExperienciaProfissional;
import com.fatec.estudamaisbackend.entity.Professor;
import com.fatec.estudamaisbackend.mappers.ExperienciaProfissionalMapper;
import com.fatec.estudamaisbackend.repository.ExperienciaProfissionalRepository;
import com.fatec.estudamaisbackend.repository.UsuarioRepository;
import com.fatec.estudamaisbackend.service.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class ExperienciaProfissionalService {

    private final ExperienciaProfissionalRepository experienciaRepository;
    private final UsuarioRepository usuarioRepository;
    private final ExperienciaProfissionalMapper experienciaMapper;

    public ExperienciaProfissionalService(ExperienciaProfissionalRepository experienciaRepository,
                                          UsuarioRepository usuarioRepository,
                                          ExperienciaProfissionalMapper experienciaMapper) {
        this.experienciaRepository = experienciaRepository;
        this.usuarioRepository = usuarioRepository;
        this.experienciaMapper = experienciaMapper;
    }

    public ExperienciaProfissionalDTO create(Long professorId, ExperienciaProfissionalDTO dto) {
        var opt = usuarioRepository.findById(professorId);
        if (opt.isEmpty() || !(opt.get() instanceof Professor)) {
            throw new ResourceNotFoundException("Professor não encontrado com id: " + professorId);
        }
        Professor p = (Professor) opt.get();
        ExperienciaProfissional e = experienciaMapper.toEntity(dto);
        e.setProfessor(p);
        ExperienciaProfissional salvo = experienciaRepository.save(e);
        return experienciaMapper.toDto(salvo);
    }

    public ExperienciaProfissionalDTO findById(Long id) {
        ExperienciaProfissional e = experienciaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Experiência não encontrada com id: " + id));
        return experienciaMapper.toDto(e);
    }

    public ExperienciaProfissionalDTO update(Long id, ExperienciaProfissionalDTO dto) {
        ExperienciaProfissional existing = experienciaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Experiência não encontrada com id: " + id));
        if (dto.getCargo() != null) existing.setPosition(dto.getCargo());
        if (dto.getInstituicao() != null) existing.setInstitution(dto.getInstituicao());
        if (dto.getPeriodo() != null) existing.setPeriod(dto.getPeriodo());
        if (dto.getDescricao() != null) existing.setDescription(dto.getDescricao());
        ExperienciaProfissional salvo = experienciaRepository.save(existing);
        return experienciaMapper.toDto(salvo);
    }

    public void delete(Long id) {
        if (!experienciaRepository.existsById(id)) {
            throw new ResourceNotFoundException("Experiência não encontrada com id: " + id);
        }
        experienciaRepository.deleteById(id);
    }
}