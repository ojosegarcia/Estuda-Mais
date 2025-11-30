package com.fatec.estudamaisbackend.controller;

import com.fatec.estudamaisbackend.entity.DiaSemana; // Importante!
import com.fatec.estudamaisbackend.entity.Disponibilidade;
import com.fatec.estudamaisbackend.entity.Professor;
import com.fatec.estudamaisbackend.repository.DisponibilidadeRepository;
import com.fatec.estudamaisbackend.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/disponibilidades")
@CrossOrigin(origins = "*") // Opcional, já que removemos do SecurityConfig, mas aqui garante se algo falhar
public class DisponibilidadeController {

    @Autowired
    private DisponibilidadeRepository disponibilidadeRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @PostMapping
    public ResponseEntity<?> criar(@RequestBody DisponibilidadeRequest req) {
        // Suporta tanto professorId quanto idProfessor (frontend envia idProfessor)
        Long profId = req.getIdProfessor() != null ? req.getIdProfessor() : req.getProfessorId();
        
        if (profId == null) {
            return ResponseEntity.badRequest().body("ID do professor é obrigatório.");
        }
        
        Optional<?> opt = usuarioRepository.findById(profId);
        
        // Verifica se existe e se é instância de Professor
        if (opt.isEmpty() || !(opt.get() instanceof Professor)) {
            return ResponseEntity.badRequest().body("Professor inválido ou não encontrado.");
        }

        Professor prof = (Professor) opt.get();

        try {
            Disponibilidade d = new Disponibilidade();
            d.setProfessor(prof);
            
            // CORREÇÃO AQUI: Converter String -> Enum
            d.setDiaSemana(DiaSemana.valueOf(req.getDiaSemana().toUpperCase())); 
            
            d.setHorarioInicio(LocalTime.parse(req.getHorarioInicio()));
            d.setHorarioFim(LocalTime.parse(req.getHorarioFim()));
            d.setAtivo(req.getAtivo() == null ? true : req.getAtivo());

            Disponibilidade salvo = disponibilidadeRepository.save(d);
            return ResponseEntity.ok(salvo);
            
        } catch (IllegalArgumentException e) {
            // Captura erro se o dia da semana estiver escrito errado (Ex: "SEGUNDA-FEIRA" em vez de "SEGUNDA")
            return ResponseEntity.badRequest().body("Dia da semana inválido ou formato de hora incorreto: " + e.getMessage());
        }
    }

    // Endpoint GET com query params (usado pelo frontend)
    @GetMapping
    public ResponseEntity<List<Disponibilidade>> listar(
            @RequestParam(required = false) Long idProfessor,
            @RequestParam(required = false) Boolean ativo) {
        
        if (idProfessor != null && ativo != null) {
            // Busca por professor e status ativo
            return ResponseEntity.ok(disponibilidadeRepository.findByProfessorIdAndAtivo(idProfessor, ativo));
        } else if (idProfessor != null) {
            // Busca apenas por professor
            return ResponseEntity.ok(disponibilidadeRepository.findByProfessorId(idProfessor));
        } else {
            // Retorna todas
            return ResponseEntity.ok(disponibilidadeRepository.findAll());
        }
    }

    @GetMapping("/professor/{professorId}")
    public ResponseEntity<List<Disponibilidade>> porProfessor(@PathVariable Long professorId) {
        return ResponseEntity.ok(disponibilidadeRepository.findByProfessorId(professorId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> atualizar(@PathVariable Long id, @RequestBody DisponibilidadeRequest req) {
        Optional<Disponibilidade> opt = disponibilidadeRepository.findById(id);
        if (opt.isEmpty()) return ResponseEntity.notFound().build();

        Disponibilidade d = opt.get();
        
        try {
            if (req.getDiaSemana() != null) {
                d.setDiaSemana(DiaSemana.valueOf(req.getDiaSemana().toUpperCase()));
            }
            if (req.getHorarioInicio() != null) {
                d.setHorarioInicio(LocalTime.parse(req.getHorarioInicio()));
            }
            if (req.getHorarioFim() != null) {
                d.setHorarioFim(LocalTime.parse(req.getHorarioFim()));
            }
            if (req.getAtivo() != null) {
                d.setAtivo(req.getAtivo());
            }
            
            disponibilidadeRepository.save(d);
            return ResponseEntity.ok(d);
            
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Dados inválidos (Dia ou Hora): " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletar(@PathVariable Long id) {
        if (!disponibilidadeRepository.existsById(id)) return ResponseEntity.notFound().build();
        disponibilidadeRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // DTO Interno alinhado com o frontend
    public static class DisponibilidadeRequest {
        private Long professorId;  // Para compatibilidade
        private Long idProfessor;  // Nome usado pelo frontend
        private String diaSemana;  // Ex: "SEGUNDA", "TERCA" (frontend usa este)
        private String horarioInicio; // Ex: "14:00" (frontend usa este)
        private String horarioFim;    // Ex: "18:00" (frontend usa este)
        private Boolean ativo;        // Frontend usa este
        
        // Campos antigos mantidos para retrocompatibilidade
        private String dayOfWeek;
        private String startTime;
        private String endTime;
        private Boolean active;

        // Getters e Setters
        public Long getProfessorId() { return professorId; }
        public void setProfessorId(Long professorId) { this.professorId = professorId; }
        
        public Long getIdProfessor() { return idProfessor; }
        public void setIdProfessor(Long idProfessor) { this.idProfessor = idProfessor; }
        
        public String getDiaSemana() { return diaSemana != null ? diaSemana : dayOfWeek; }
        public void setDiaSemana(String diaSemana) { this.diaSemana = diaSemana; }
        
        public String getHorarioInicio() { return horarioInicio != null ? horarioInicio : startTime; }
        public void setHorarioInicio(String horarioInicio) { this.horarioInicio = horarioInicio; }
        
        public String getHorarioFim() { return horarioFim != null ? horarioFim : endTime; }
        public void setHorarioFim(String horarioFim) { this.horarioFim = horarioFim; }
        
        public Boolean getAtivo() { return ativo != null ? ativo : active; }
        public void setAtivo(Boolean ativo) { this.ativo = ativo; }
        
        // Getters/Setters antigos
        public String getDayOfWeek() { return dayOfWeek; }
        public void setDayOfWeek(String dayOfWeek) { this.dayOfWeek = dayOfWeek; }
        public String getStartTime() { return startTime; }
        public void setStartTime(String startTime) { this.startTime = startTime; }
        public String getEndTime() { return endTime; }
        public void setEndTime(String endTime) { this.endTime = endTime; }
        public Boolean getActive() { return active; }
        public void setActive(Boolean active) { this.active = active; }
    }
}