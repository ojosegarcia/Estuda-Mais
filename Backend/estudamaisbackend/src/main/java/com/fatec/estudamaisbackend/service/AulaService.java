package com.fatec.estudamaisbackend.service;

import com.fatec.estudamaisbackend.dtos.AulaDTO;
import com.fatec.estudamaisbackend.entity.*;
import com.fatec.estudamaisbackend.mappers.AulaMapper;
import com.fatec.estudamaisbackend.repository.AulaRepository;
import com.fatec.estudamaisbackend.repository.DisponibilidadeRepository;
import com.fatec.estudamaisbackend.repository.MateriaRepository;
import com.fatec.estudamaisbackend.repository.UsuarioRepository;
import com.fatec.estudamaisbackend.service.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
@Transactional
public class AulaService {

    private final AulaRepository aulaRepository;
    private final UsuarioRepository usuarioRepository;
    private final MateriaRepository materiaRepository;
    private final DisponibilidadeRepository disponibilidadeRepository;
    private final AulaMapper aulaMapper;

    public AulaService(AulaRepository aulaRepository,
                       UsuarioRepository usuarioRepository,
                       MateriaRepository materiaRepository,
                       DisponibilidadeRepository disponibilidadeRepository,
                       AulaMapper aulaMapper) {
        this.aulaRepository = aulaRepository;
        this.usuarioRepository = usuarioRepository;
        this.materiaRepository = materiaRepository;
        this.disponibilidadeRepository = disponibilidadeRepository;
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
        aula.setValorAula(dto.getValorAula());
        aula.setLinkReuniao(dto.getLinkReuniao());
        aula.setStatusAula(dto.getStatusAula() != null ? StatusAula.valueOf(dto.getStatusAula()) : StatusAula.SOLICITADA);

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
        if (dto.getValorAula() != null) existing.setValorAula(dto.getValorAula());
        if (dto.getLinkReuniao() != null) existing.setLinkReuniao(dto.getLinkReuniao());
        if (dto.getStatusAula() != null) existing.setStatusAula(StatusAula.valueOf(dto.getStatusAula()));

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

    // ========== NOVOS MÉTODOS PARA AGENDAMENTO ==========

    /**
     * Busca aulas de um professor em uma data específica
     * Usado pelo frontend para verificar slots ocupados
     */
    public List<Aula> findByProfessorIdAndDataAula(Long professorId, LocalDate dataAula) {
        return aulaRepository.findByProfessorIdAndDataAula(professorId, dataAula);
    }

    /**
     * Valida se o agendamento é possível
     * Validações:
     * 1. Não permitir agendar no passado
     * 2. Verificar se o slot já está ocupado (conflito de horário)
     * 3. Validar se professor trabalha no dia/horário solicitado
     */
    public void validarAgendamento(Long professorId, LocalDate dataAula, LocalTime horarioInicio, LocalTime horarioFim) {
        // 1. Validar data no passado
        if (dataAula.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Não é possível agendar aulas em datas passadas");
        }

        // 2. Verificar conflito de horário
        List<Aula> conflitos = aulaRepository.findConflitosDeHorario(professorId, dataAula, horarioInicio, horarioFim);
        if (!conflitos.isEmpty()) {
            throw new IllegalArgumentException("Este horário já está ocupado. Por favor, escolha outro horário.");
        }

        // 3. Validar se professor trabalha neste dia/horário
        DayOfWeek diaDaSemana = dataAula.getDayOfWeek();
        DiaSemana diaSemanaEnum = converterDayOfWeekParaDiaSemana(diaDaSemana);
        
        List<Disponibilidade> disponibilidades = disponibilidadeRepository.findByProfessorIdAndAtivo(professorId, true);
        
        boolean professorTrabalhaNesteDia = disponibilidades.stream()
            .anyMatch(disp -> 
                disp.getDiaSemana() == diaSemanaEnum &&
                !horarioInicio.isBefore(disp.getHorarioInicio()) &&
                !horarioFim.isAfter(disp.getHorarioFim())
            );

        if (!professorTrabalhaNesteDia) {
            throw new IllegalArgumentException("Professor não trabalha neste dia/horário. Por favor, escolha outro horário disponível.");
        }
    }

    /**
     * Aceita uma aula e adiciona o link da reunião obrigatoriamente
     * Validação: Link é obrigatório ao aceitar (status = CONFIRMADA)
     */
    public Aula aceitarAulaComLink(Long aulaId, String linkReuniao) {
        Aula aula = findById(aulaId);

        // Validação: Link obrigatório
        if (linkReuniao == null || linkReuniao.trim().isEmpty()) {
            throw new IllegalArgumentException("Link da reunião é obrigatório para aceitar a aula");
        }

        // Validação opcional: verificar se o link contém plataformas válidas
        String linkLower = linkReuniao.toLowerCase();
        boolean linkValido = linkLower.contains("zoom.us") || 
                            linkLower.contains("meet.google.com") || 
                            linkLower.contains("teams.microsoft.com") ||
                            linkLower.contains("teams.live.com") ||
                            linkLower.startsWith("http://") ||
                            linkLower.startsWith("https://");

        if (!linkValido) {
            throw new IllegalArgumentException("Link inválido. Use links de plataformas como Zoom, Google Meet ou Microsoft Teams");
        }

        aula.setStatusAula(StatusAula.CONFIRMADA);
        aula.setLinkReuniao(linkReuniao.trim());

        return aulaRepository.save(aula);
    }

    /**
     * Remove aula com lógica de soft delete
     * - 1ª remoção: marca flag removidoPeloAluno ou removidoPeloProfessor
     * - 2ª remoção (quando ambos removeram): DELETE permanente
     */
    public void removerAulaPorUsuario(Long aulaId, Long usuarioId, String tipoUsuario) {
        Aula aula = findById(aulaId);

        boolean ehProfessor = "PROFESSOR".equalsIgnoreCase(tipoUsuario);
        boolean ehAluno = "ALUNO".equalsIgnoreCase(tipoUsuario);

        if (!ehProfessor && !ehAluno) {
            throw new IllegalArgumentException("Tipo de usuário inválido");
        }

        // Verifica se o outro lado já removeu
        boolean alunoJaRemoveu = aula.getRemovidoPeloAluno() != null && aula.getRemovidoPeloAluno();
        boolean professorJaRemoveu = aula.getRemovidoPeloProfessor() != null && aula.getRemovidoPeloProfessor();

        // Se AMBOS já removeram, deleta permanentemente
        if ((ehProfessor && alunoJaRemoveu) || (ehAluno && professorJaRemoveu)) {
            aulaRepository.deleteById(aulaId);
            return;
        }

        // Caso contrário, apenas marca como removido
        if (ehProfessor) {
            aula.setRemovidoPeloProfessor(true);
        } else {
            aula.setRemovidoPeloAluno(true);
        }

        aulaRepository.save(aula);
    }

    /**
     * Atualiza parcialmente uma aula (para PATCH)
     */
    public Aula atualizarParcial(Long id, Aula atualizacoes) {
        Aula existing = findById(id);

        if (atualizacoes.getStatusAula() != null) {
            existing.setStatusAula(atualizacoes.getStatusAula());
        }
        if (atualizacoes.getLinkReuniao() != null) {
            existing.setLinkReuniao(atualizacoes.getLinkReuniao());
        }
        if (atualizacoes.getRemovidoPeloAluno() != null) {
            existing.setRemovidoPeloAluno(atualizacoes.getRemovidoPeloAluno());
        }
        if (atualizacoes.getRemovidoPeloProfessor() != null) {
            existing.setRemovidoPeloProfessor(atualizacoes.getRemovidoPeloProfessor());
        }

        return aulaRepository.save(existing);
    }

    /**
     * Converte DayOfWeek (Java) para DiaSemana (Enum customizado)
     */
    private DiaSemana converterDayOfWeekParaDiaSemana(DayOfWeek dayOfWeek) {
        return switch (dayOfWeek) {
            case MONDAY -> DiaSemana.SEGUNDA;
            case TUESDAY -> DiaSemana.TERCA;
            case WEDNESDAY -> DiaSemana.QUARTA;
            case THURSDAY -> DiaSemana.QUINTA;
            case FRIDAY -> DiaSemana.SEXTA;
            case SATURDAY -> DiaSemana.SABADO;
            case SUNDAY -> DiaSemana.DOMINGO;
        };
    }
}
