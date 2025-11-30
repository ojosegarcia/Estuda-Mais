package com.fatec.estudamaisbackend.service;

import com.fatec.estudamaisbackend.dtos.ConquistaDTO;
import com.fatec.estudamaisbackend.entity.Conquista;
import com.fatec.estudamaisbackend.entity.Professor;
import com.fatec.estudamaisbackend.mappers.ConquistaMapper;
import com.fatec.estudamaisbackend.repository.ConquistaRepository;
import com.fatec.estudamaisbackend.repository.UsuarioRepository;
import com.fatec.estudamaisbackend.service.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ConquistaService {

    private static final int MAX_CONQUISTAS = 5;

    private final ConquistaRepository conquistaRepository;
    private final UsuarioRepository usuarioRepository;
    private final ConquistaMapper conquistaMapper;

    public ConquistaService(ConquistaRepository conquistaRepository,
                            UsuarioRepository usuarioRepository,
                            ConquistaMapper conquistaMapper) {
        this.conquistaRepository = conquistaRepository;
        this.usuarioRepository = usuarioRepository;
        this.conquistaMapper = conquistaMapper;
    }

    public ConquistaDTO create(Long professorId, ConquistaDTO dto) {
        var opt = usuarioRepository.findById(professorId);
        if (opt.isEmpty() || !(opt.get() instanceof Professor)) {
            throw new ResourceNotFoundException("Professor não encontrado com id: " + professorId);
        }
        
        // ✅ Validação: máximo 5 conquistas por professor
        long count = conquistaRepository.countByProfessorId(professorId);
        if (count >= MAX_CONQUISTAS) {
            throw new RuntimeException("Professor já possui o máximo de " + MAX_CONQUISTAS + " conquistas");
        }
        
        Professor p = (Professor) opt.get();
        Conquista c = conquistaMapper.toEntity(dto);
        c.setProfessor(p);
        Conquista salvo = conquistaRepository.save(c);
        return conquistaMapper.toDto(salvo);
    }

    public ConquistaDTO findById(Long id) {
        Conquista c = conquistaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Conquista não encontrada com id: " + id));
        return conquistaMapper.toDto(c);
    }

    public ConquistaDTO update(Long id, ConquistaDTO dto) {
        Conquista existing = conquistaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Conquista não encontrada com id: " + id));
        if (dto.getTituloConquista() != null) existing.setTituloConquista(dto.getTituloConquista());
        if (dto.getAno() != null) existing.setAno(dto.getAno());
        if (dto.getDescricao() != null) existing.setDescricao(dto.getDescricao());
        Conquista salvo = conquistaRepository.save(existing);
        return conquistaMapper.toDto(salvo);
    }

    public void delete(Long id) {
        if (!conquistaRepository.existsById(id)) {
            throw new ResourceNotFoundException("Conquista não encontrada com id: " + id);
        }
        conquistaRepository.deleteById(id);
    }

    public java.util.List<ConquistaDTO> findByProfessorId(Long professorId) {
        return conquistaRepository.findByProfessorIdOrderByAnoDesc(professorId)
                .stream()
                .map(conquistaMapper::toDto)
                .collect(java.util.stream.Collectors.toList());
    }
}