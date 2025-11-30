package com.fatec.estudamaisbackend.controller;

import com.fatec.estudamaisbackend.entity.Aula;
import com.fatec.estudamaisbackend.entity.Aluno;
import com.fatec.estudamaisbackend.entity.Professor;
import com.fatec.estudamaisbackend.entity.Materia;
import com.fatec.estudamaisbackend.entity.StatusAula;
import com.fatec.estudamaisbackend.repository.AulaRepository;
import com.fatec.estudamaisbackend.repository.MateriaRepository;
import com.fatec.estudamaisbackend.repository.UsuarioRepository;
import com.fatec.estudamaisbackend.entity.Usuario;
import com.fatec.estudamaisbackend.service.AulaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * AulaController - Gerencia o agendamento e ciclo de vida das aulas
 * Segue padrão REST do frontend Angular
 */
@RestController
@RequestMapping("/api/aulas")
@CrossOrigin(
    origins = "*",
    methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.PATCH, RequestMethod.DELETE, RequestMethod.OPTIONS},
    allowedHeaders = "*",
    allowCredentials = "false"
)
public class AulaController {

    @Autowired
    private AulaService aulaService;

    @Autowired
    private AulaRepository aulaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private MateriaRepository materiaRepository;

    /**
     * GET /api/aulas - Lista aulas com filtros opcionais
     * Query params: idAluno, idProfessor, dataAula
     * Aplica filtro de soft delete (removidoPeloAluno/removidoPeloProfessor)
     */
    @GetMapping
    public ResponseEntity<List<Aula>> listarAulas(
            @RequestParam(required = false) Long idAluno,
            @RequestParam(required = false) Long idProfessor,
            @RequestParam(required = false) String dataAula) {

        List<Aula> aulas;

        // Busca por professor e data (para verificar slots ocupados)
        if (idProfessor != null && dataAula != null) {
            LocalDate data = LocalDate.parse(dataAula);
            aulas = aulaService.findByProfessorIdAndDataAula(idProfessor, data);
        }
        // Busca por aluno (filtra as que ele não removeu)
        else if (idAluno != null) {
            aulas = aulaRepository.findByAlunoIdAndRemovidoPeloAluno(idAluno, false);
        }
        // Busca por professor (filtra as que ele não removeu)
        else if (idProfessor != null) {
            aulas = aulaRepository.findByProfessorIdAndRemovidoPeloProfessor(idProfessor, false);
        }
        // Lista todas (sem filtro de soft delete)
        else {
            aulas = aulaRepository.findAll();
        }

        return ResponseEntity.ok(aulas);
    }

    /**
     * GET /api/aulas/{id} - Busca aula por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<Aula> buscarPorId(@PathVariable Long id) {
        try {
            Aula aula = aulaService.findById(id);
            return ResponseEntity.ok(aula);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * POST /api/aulas - Solicita uma nova aula
     * Aplica validações:
     * - Não permitir agendar no passado
     * - Verificar conflito de horário
     * - Validar se professor trabalha no dia/horário
     */
    @PostMapping
    public ResponseEntity<?> solicitarAula(@RequestBody AulaRequest request) {
        try {
            // Validar entidades
            Optional<Usuario> optProf = usuarioRepository.findById(request.getIdProfessor());
            Optional<Usuario> optAluno = usuarioRepository.findById(request.getIdAluno());
            Optional<Materia> optMat = materiaRepository.findById(request.getIdMateria());

            if (optProf.isEmpty() || optAluno.isEmpty() || optMat.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Professor, aluno ou matéria não encontrados"));
            }

            if (!(optProf.get() instanceof Professor) || !(optAluno.get() instanceof Aluno)) {
                return ResponseEntity.badRequest().body(Map.of("error", "Tipo de usuário inválido"));
            }

            // Parse data/hora
            LocalDate dataAula = LocalDate.parse(request.getDataAula());
            LocalTime horarioInicio = LocalTime.parse(request.getHorarioInicio());
            LocalTime horarioFim = LocalTime.parse(request.getHorarioFim());

            // VALIDAÇÕES DE NEGÓCIO
            aulaService.validarAgendamento(
                request.getIdProfessor(),
                dataAula,
                horarioInicio,
                horarioFim
            );

            // Criar aula
            Aula aula = new Aula();
            aula.setProfessor((Professor) optProf.get());
            aula.setAluno((Aluno) optAluno.get());
            aula.setMateria(optMat.get());
            aula.setDataAula(dataAula);
            aula.setHorarioInicio(horarioInicio);
            aula.setHorarioFim(horarioFim);
            aula.setStatusAula(StatusAula.SOLICITADA);
            aula.setValorAula(request.getValorAula());
            aula.setLinkReuniao(request.getLinkReuniao());
            aula.setDataCriacao(LocalDateTime.now());
            aula.setRemovidoPeloAluno(false);
            aula.setRemovidoPeloProfessor(false);

            Aula salvo = aulaRepository.save(aula);
            return ResponseEntity.status(HttpStatus.CREATED).body(salvo);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Erro ao criar aula: " + e.getMessage()));
        }
    }

    /**
     * PATCH /api/aulas/{id} - Atualização parcial
     * Usado para: atualizar status, link, flags de remoção
     */
    @PatchMapping("/{id}")
    public ResponseEntity<?> atualizarParcial(@PathVariable Long id, @RequestBody Map<String, Object> updates) {
        try {
            Aula aula = aulaService.findById(id);
            Aula atualizacoes = new Aula();

            // Processa cada campo enviado
            if (updates.containsKey("statusAula")) {
                atualizacoes.setStatusAula(StatusAula.valueOf((String) updates.get("statusAula")));
            }
            if (updates.containsKey("linkReuniao")) {
                atualizacoes.setLinkReuniao((String) updates.get("linkReuniao"));
            }
            if (updates.containsKey("removidoPeloAluno")) {
                atualizacoes.setRemovidoPeloAluno((Boolean) updates.get("removidoPeloAluno"));
            }
            if (updates.containsKey("removidoPeloProfessor")) {
                atualizacoes.setRemovidoPeloProfessor((Boolean) updates.get("removidoPeloProfessor"));
            }

            Aula atualizada = aulaService.atualizarParcial(id, atualizacoes);
            return ResponseEntity.ok(atualizada);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * PUT /api/aulas/{id}/aceitar-com-link - Aceita aula com link obrigatório
     * Validações:
     * - Link obrigatório
     * - Link deve conter plataforma válida (Zoom, Meet, Teams)
     */
    @PutMapping("/{id}/aceitar-com-link")
    public ResponseEntity<?> aceitarComLink(@PathVariable Long id, @RequestBody Map<String, String> body) {
        try {
            String linkReuniao = body.get("linkReuniao");
            Aula aulaAtualizada = aulaService.aceitarAulaComLink(id, linkReuniao);
            return ResponseEntity.ok(aulaAtualizada);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Erro ao aceitar aula: " + e.getMessage()));
        }
    }

    /**
     * DELETE /api/aulas/{id} - Remove aula com soft delete
     * - 1ª remoção: marca flag removidoPeloAluno/removidoPeloProfessor
     * - 2ª remoção (quando ambos removeram): DELETE permanente
     * 
     * Requer query param: usuarioId e tipoUsuario
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> removerAula(
            @PathVariable Long id,
            @RequestParam Long usuarioId,
            @RequestParam String tipoUsuario) {
        try {
            aulaService.removerAulaPorUsuario(id, usuarioId, tipoUsuario);
            return ResponseEntity.ok(Map.of("message", "Aula removida com sucesso"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ========== ENDPOINTS LEGADOS (mantidos para compatibilidade) ==========

    @GetMapping("/aluno/{alunoId}")
    public ResponseEntity<List<Aula>> aulasDoAluno(@PathVariable Long alunoId) {
        return ResponseEntity.ok(aulaRepository.findByAlunoIdAndRemovidoPeloAluno(alunoId, false));
    }

    @GetMapping("/professor/{professorId}")
    public ResponseEntity<List<Aula>> aulasDoProfessor(@PathVariable Long professorId) {
        return ResponseEntity.ok(aulaRepository.findByProfessorIdAndRemovidoPeloProfessor(professorId, false));
    }

    @PutMapping("/{id}/aceitar")
    public ResponseEntity<?> aceitar(@PathVariable Long id) {
        return atualizarStatus(id, StatusAula.CONFIRMADA);
    }

    @PutMapping("/{id}/recusar")
    public ResponseEntity<?> recusar(@PathVariable Long id) {
        return atualizarStatus(id, StatusAula.RECUSADA);
    }

    @PutMapping("/{id}/cancelar")
    public ResponseEntity<?> cancelar(@PathVariable Long id) {
        return atualizarStatus(id, StatusAula.CANCELADA);
    }

    @PutMapping("/{id}/realizar")
    public ResponseEntity<?> realizar(@PathVariable Long id) {
        return atualizarStatus(id, StatusAula.REALIZADA);
    }

    private ResponseEntity<?> atualizarStatus(Long id, StatusAula novoStatus) {
        try {
            Aula aula = aulaService.findById(id);
            aula.setStatusAula(novoStatus);
            aulaRepository.save(aula);
            return ResponseEntity.ok(aula);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    // ========== DTOs ==========

    public static class AulaRequest {
        private Long idProfessor;
        private Long idAluno;
        private Long idMateria;
        private String dataAula;
        private String horarioInicio;
        private String horarioFim;
        private Double valorAula;
        private String linkReuniao;

        public Long getIdProfessor() { return idProfessor; }
        public void setIdProfessor(Long idProfessor) { this.idProfessor = idProfessor; }
        public Long getIdAluno() { return idAluno; }
        public void setIdAluno(Long idAluno) { this.idAluno = idAluno; }
        public Long getIdMateria() { return idMateria; }
        public void setIdMateria(Long idMateria) { this.idMateria = idMateria; }
        public String getDataAula() { return dataAula; }
        public void setDataAula(String dataAula) { this.dataAula = dataAula; }
        public String getHorarioInicio() { return horarioInicio; }
        public void setHorarioInicio(String horarioInicio) { this.horarioInicio = horarioInicio; }
        public String getHorarioFim() { return horarioFim; }
        public void setHorarioFim(String horarioFim) { this.horarioFim = horarioFim; }
        public Double getValorAula() { return valorAula; }
        public void setValorAula(Double valorAula) { this.valorAula = valorAula; }
        public String getLinkReuniao() { return linkReuniao; }
        public void setLinkReuniao(String linkReuniao) { this.linkReuniao = linkReuniao; }
    }
}
