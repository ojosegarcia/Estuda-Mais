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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

/**
 * AulaController corrigido para usar os tipos reais da entidade:
 * - status com enum StatusAula
 * - classValue como Double (usando setClassValue(Double))
 */
@RestController
@RequestMapping("/api/aulas")
@CrossOrigin
public class AulaController {

    @Autowired
    private AulaRepository aulaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private MateriaRepository materiaRepository;

    @PostMapping
    public ResponseEntity<?> solicitarAula(@RequestBody AulaRequest request) {
        Optional<Usuario> optProf = usuarioRepository.findById(request.getIdProfessor());
        Optional<Usuario> optAluno = usuarioRepository.findById(request.getIdAluno());
        Optional<Materia> optMat = materiaRepository.findById(request.getIdMateria());

        if (optProf.isEmpty() || optAluno.isEmpty() || optMat.isEmpty()) {
            return ResponseEntity.badRequest().body("professor/aluno/materia inválidos");
        }

        if (!(optProf.get() instanceof Professor) || !(optAluno.get() instanceof Aluno)) {
            return ResponseEntity.badRequest().body("tipo de usuário inválido para professor/aluno");
        }

        Aula aula = new Aula();
        aula.setProfessor((Professor) optProf.get());
        aula.setAluno((Aluno) optAluno.get());
        aula.setMateria(optMat.get());

        // parse date/time
        try {
            aula.setDataAula(LocalDate.parse(request.getDataAula()));
            aula.setHorarioInicio(LocalTime.parse(request.getHorarioInicio()));
            aula.setHorarioFim(LocalTime.parse(request.getHorarioFim()));
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body("formato de data/hora inválido. dataAula yyyy-MM-dd, horario HH:mm");
        }

        // Use enum StatusAula (entidade espera StatusAula, não String)
        aula.setStatus(StatusAula.SOLICITADA);

        // entidade espera Double no setter setClassValue(Double) => use request.getValorAula() diretamente
        if (request.getValorAula() != null) {
            aula.setClassValue(request.getValorAula());
        }

        aula.setMeetingLink(request.getLinkReuniao());
        aula.setCreatedAt(LocalDateTime.now());

        Aula salvo = aulaRepository.save(aula);
        return ResponseEntity.ok(salvo);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Aula> buscar(@PathVariable Long id) {
        Optional<Aula> opt = aulaRepository.findById(id);
        return opt.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/aluno/{alunoId}")
    public ResponseEntity<List<Aula>> aulasDoAluno(@PathVariable Long alunoId) {
        return ResponseEntity.ok(aulaRepository.findByAlunoId(alunoId));
    }

    @GetMapping("/professor/{professorId}")
    public ResponseEntity<List<Aula>> aulasDoProfessor(@PathVariable Long professorId) {
        return ResponseEntity.ok(aulaRepository.findByProfessorId(professorId));
    }

    private ResponseEntity<?> atualizarStatus(Long id, StatusAula novoStatus) {
        Optional<Aula> opt = aulaRepository.findById(id);
        if (opt.isEmpty()) return ResponseEntity.notFound().build();
        Aula aula = opt.get();
        aula.setStatus(novoStatus);
        aulaRepository.save(aula);
        return ResponseEntity.ok(aula);
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

    // Request DTO for creating an Aula
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