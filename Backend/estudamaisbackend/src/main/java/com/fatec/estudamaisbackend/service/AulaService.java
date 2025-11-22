package com.fatec.estudamaisbackend.service;

import com.fatec.estudamaisbackend.dtos.AulaDTO;
import com.fatec.estudamaisbackend.entity.*;
import com.fatec.estudamaisbackend.mappers.AulaMapper;
import com.fatec.estudamaisbackend.repository.AulaRepository;
import com.fatec.estudamaisbackend.repository.MateriaRepository;
import com.fatec.estudamaisbackend.repository.UsuarioRepository;
import com.fatec.estudamaisbackend.service.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
@Transactional
public class AulaService {

    private final AulaRepository aulaRepository;
    private final UsuarioRepository usuarioRepository;
    private final MateriaRepository materiaRepository;
    private final AulaMapper aulaMapper;

    public AulaService(AulaRepository aulaRepository,
                       UsuarioRepository usuarioRepository,
                       MateriaRepository materiaRepository,
                       AulaMapper aulaMapper) {
        this.aulaRepository = aulaRepository;
        this.usuarioRepository = usuarioRepository;
        this.materiaRepository = materiaRepository;
        this.aulaMapper = aulaMapper;
    }

    public List<Aula> findAll() {
        return aulaRepository.findAll();
    }

    public Aula findById(Long id) {
        return aulaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Aula não encontrada com id: " + id));
    }

    public AulaDTO createFromDto(AulaDTO dto) {
        Usuario prof = usuarioRepository.findById(dto.getIdProfessor())
                .orElseThrow(() -> new ResourceNotFoundException("Professor não encontrado com id: " + dto.getIdProfessor()));
        Usuario aluno = usuarioRepository.findById(dto.getIdAluno())
                .orElseThrow(() -> new ResourceNotFoundException("Aluno não encontrado com id: " + dto.getIdAluno()));
        Materia materia = materiaRepository.findById(dto.getIdMateria())
                .orElseThrow(() -> new ResourceNotFoundException("Matéria não encontrada com id: " + dto.getIdMateria()));

        if (!(prof instanceof Professor)) throw new IllegalArgumentException("idProfessor inválido");
        if (!(aluno instanceof Aluno)) throw new IllegalArgumentException("idAluno inválido");

        Aula aula = new Aula();
        if (dto.getDataAula() != null) aula.setDataAula(dto.getDataAula());
        if (dto.getHorarioInicio() != null) aula.setHorarioInicio(dto.getHorarioInicio());
        if (dto.getHorarioFim() != null) aula.setHorarioFim(dto.getHorarioFim());
        aula.setProfessor((Professor) prof);
        aula.setAluno((Aluno) aluno);
        aula.setMateria(materia);
        aula.setClassValue(dto.getValorAula());
        aula.setMeetingLink(dto.getLinkReuniao());
        aula.setStatus(dto.getStatusAula() != null ? StatusAula.valueOf(dto.getStatusAula()) : StatusAula.SOLICITADA);

        Aula salvo = aulaRepository.save(aula);
        return aulaMapper.toDto(salvo);
    }

    public AulaDTO updateFromDto(Long id, AulaDTO dto) {
        Aula existing = findById(id);

        // handle relations if ids provided
        if (dto.getIdProfessor() != null) {
            Usuario p = usuarioRepository.findById(dto.getIdProfessor())
                    .orElseThrow(() -> new ResourceNotFoundException("Professor não encontrado: " + dto.getIdProfessor()));
            if (!(p instanceof Professor)) throw new IllegalArgumentException("idProfessor inválido");
            existing.setProfessor((Professor) p);
        }
        if (dto.getIdAluno() != null) {
            Usuario a = usuarioRepository.findById(dto.getIdAluno())
                    .orElseThrow(() -> new ResourceNotFoundException("Aluno não encontrado: " + dto.getIdAluno()));
            if (!(a instanceof Aluno)) throw new IllegalArgumentException("idAluno inválido");
            existing.setAluno((Aluno) a);
        }
        if (dto.getIdMateria() != null) {
            Materia m = materiaRepository.findById(dto.getIdMateria())
                    .orElseThrow(() -> new ResourceNotFoundException("Matéria não encontrada: " + dto.getIdMateria()));
            existing.setMateria(m);
        }

        // simple fields
        if (dto.getDataAula() != null) existing.setDataAula(dto.getDataAula());
        if (dto.getHorarioInicio() != null) existing.setHorarioInicio(dto.getHorarioInicio());
        if (dto.getHorarioFim() != null) existing.setHorarioFim(dto.getHorarioFim());
        if (dto.getValorAula() != null) existing.setClassValue(dto.getValorAula());
        if (dto.getLinkReuniao() != null) existing.setMeetingLink(dto.getLinkReuniao());
        if (dto.getStatusAula() != null) existing.setStatus(StatusAula.valueOf(dto.getStatusAula()));

        Aula salvo = aulaRepository.save(existing);
        return aulaMapper.toDto(salvo);
    }

    public List<Aula> findByAlunoId(Long alunoId) {
        return aulaRepository.findByAlunoId(alunoId);
    }

    public List<Aula> findByProfessorId(Long professorId) {
        return aulaRepository.findByProfessorId(professorId);
    }

    public void delete(Long id) {
        if (!aulaRepository.existsById(id)) {
            throw new ResourceNotFoundException("Aula não encontrada com id: " + id);
        }
        aulaRepository.deleteById(id);
    }
}