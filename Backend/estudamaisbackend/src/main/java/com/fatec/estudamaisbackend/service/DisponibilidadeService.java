package com.fatec.estudamaisbackend.service;

import com.fatec.estudamaisbackend.dtos.DisponibilidadeDTO;
import com.fatec.estudamaisbackend.entity.Disponibilidade;
import com.fatec.estudamaisbackend.entity.Professor;
import com.fatec.estudamaisbackend.mappers.DisponibilidadeMapper;
import com.fatec.estudamaisbackend.repository.DisponibilidadeRepository;
import com.fatec.estudamaisbackend.repository.UsuarioRepository;
import com.fatec.estudamaisbackend.service.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class DisponibilidadeService {

    private final DisponibilidadeRepository disponibilidadeRepository;
    private final UsuarioRepository usuarioRepository;
    private final DisponibilidadeMapper disponibilidadeMapper;

    public DisponibilidadeService(DisponibilidadeRepository disponibilidadeRepository,
                                  UsuarioRepository usuarioRepository,
                                  DisponibilidadeMapper disponibilidadeMapper) {
        this.disponibilidadeRepository = disponibilidadeRepository;
        this.usuarioRepository = usuarioRepository;
        this.disponibilidadeMapper = disponibilidadeMapper;
    }

    public DisponibilidadeDTO create(Long professorId, DisponibilidadeDTO dto) {
        var opt = usuarioRepository.findById(professorId);
        if (opt.isEmpty() || !(opt.get() instanceof Professor)) {
            throw new ResourceNotFoundException("Professor não encontrado com id: " + professorId);
        }
        Professor p = (Professor) opt.get();
        Disponibilidade d = disponibilidadeMapper.toEntity(dto);
        d.setProfessor(p);
        Disponibilidade salvo = disponibilidadeRepository.save(d);
        return disponibilidadeMapper.toDto(salvo);
    }

    public DisponibilidadeDTO findById(Long id) {
        Disponibilidade d = disponibilidadeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Disponibilidade não encontrada com id: " + id));
        return disponibilidadeMapper.toDto(d);
    }

    public List<Disponibilidade> findByProfessorId(Long professorId) {
        return disponibilidadeRepository.findByProfessorId(professorId);
    }

    public DisponibilidadeDTO update(Long id, DisponibilidadeDTO dto) {
        Disponibilidade existing = disponibilidadeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Disponibilidade não encontrada com id: " + id));
        // update simple fields via mapper -> entity, then set professor preserved
        existing.setDayOfWeek(dto.getDiaSemana());
        existing.setStartTime(dto.getHorarioInicio());
        existing.setEndTime(dto.getHorarioFim());
        existing.setActive(dto.getAtivo());
        Disponibilidade salvo = disponibilidadeRepository.save(existing);
        return disponibilidadeMapper.toDto(salvo);
    }

    public void delete(Long id) {
        if (!disponibilidadeRepository.existsById(id)) {
            throw new ResourceNotFoundException("Disponibilidade não encontrada com id: " + id);
        }
        disponibilidadeRepository.deleteById(id);
    }
}