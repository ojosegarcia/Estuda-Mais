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
        Optional<?> opt = usuarioRepository.findById(professorId);
        if (opt.isEmpty() || !(opt.get() instanceof Professor)) {
            throw new ResourceNotFoundException("Professor não encontrado com id: " + professorId);
        }
        Professor p = (Professor) opt.get();
        
        Disponibilidade d = disponibilidadeMapper.toEntity(dto);
        d.setProfessor(p);
        
        Disponibilidade salvo = disponibilidadeRepository.save(d);
        return disponibilidadeMapper.toDto(salvo);
    }

    @Transactional(readOnly = true)
    public DisponibilidadeDTO findById(Long id) {
        Disponibilidade d = disponibilidadeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Disponibilidade não encontrada com id: " + id));
        return disponibilidadeMapper.toDto(d);
    }

    @Transactional(readOnly = true)
    public List<DisponibilidadeDTO> findByProfessorId(Long professorId) {
        List<Disponibilidade> lista = disponibilidadeRepository.findByProfessorId(professorId);
        return disponibilidadeMapper.toDtoList(lista);
    }

    public DisponibilidadeDTO update(Long id, DisponibilidadeDTO dto) {
        Disponibilidade existing = disponibilidadeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Disponibilidade não encontrada com id: " + id));
        
        // Atualiza campos simples
        existing.setDiaSemana(dto.getDiaSemana());
        existing.setHorarioInicio(dto.getHorarioInicio()); // Corrigido de setHoraInicio
        existing.setHorarioFim(dto.getHorarioFim());       // Corrigido de setHoraFim
        existing.setAtivo(dto.getAtivo());
        
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