package br.ufpr.avaliacao.service;

import br.ufpr.avaliacao.dao.jdbc.CursoDao;
import br.ufpr.avaliacao.dao.jdbc.DisciplinaDao;
import br.ufpr.avaliacao.dao.jdbc.TurmaDao;
import br.ufpr.avaliacao.dao.jdbc.UsuarioDao;
import br.ufpr.avaliacao.model.Curso;
import br.ufpr.avaliacao.model.Disciplina;
import br.ufpr.avaliacao.model.Turma;
import br.ufpr.avaliacao.model.Usuario;
import br.ufpr.avaliacao.model.Perfil;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Camada de Serviço responsável pela gestão de Cursos, Disciplinas e Turmas 
 * (Configuração do Contexto Avaliativo - RF04, RF05, RF06).
 */
public class ContextoService {

    private static final Logger LOGGER = Logger.getLogger(ContextoService.class.getName());
    private final CursoDao cursoDao;
    private final DisciplinaDao disciplinaDao;
    private final TurmaDao turmaDao;
    private final UsuarioDao usuarioDao;

    public ContextoService() {
        this.cursoDao = new CursoDao();
        this.disciplinaDao = new DisciplinaDao();
        this.turmaDao = new TurmaDao();
        this.usuarioDao = new UsuarioDao();
    }
    
    // ... (Métodos para Curso e Disciplina)

    // =========================================================================
    // Métodos para Turma (RF05)
    // =========================================================================

    public Turma salvarTurma(Turma t) {
        // ... (Validações de nome, disciplinaId e existência/perfil do Professor)
        if (t.getNome() == null || t.getDisciplinaId() == null) {
            throw new IllegalArgumentException("Nome e Disciplina são obrigatórios para a Turma.");
        }
        
        if (disciplinaDao.findById(t.getDisciplinaId()) == null) {
            throw new IllegalArgumentException("A Disciplina associada não existe.");
        }
        
        // Validação: professores devem existir e ter o perfil PROFESSOR (RF05)
        if (t.getProfessoresIds() != null && !t.getProfessoresIds().isEmpty()) {
             for (Long profId : t.getProfessoresIds()) {
                Usuario prof = usuarioDao.findById(profId);
                if (prof == null || !prof.getPerfis().contains(Perfil.PROFESSOR)) {
                    throw new IllegalArgumentException("O usuário com ID " + profId + " não é um professor válido.");
                }
             }
        }
        
        return turmaDao.save(t);
    }
    
    public List<Turma> listarTurmas() {
        return turmaDao.findAll();
    }
    
    public Turma buscarTurmaPorId(Long id) {
        return turmaDao.findById(id);
    }

    // =========================================================================
    // Métodos para Matrícula (RF06)
    // =========================================================================

    /**
     * Associa um aluno a uma turma, registrando a matrícula (RF06).
     */
    public void matricularAlunoEmTurma(Long turmaId, Long alunoId) {
        // 1. Validação: A turma e o aluno devem existir e ter o perfil correto
        if (turmaDao.findById(turmaId) == null) {
            throw new IllegalArgumentException("Turma não encontrada.");
        }
        Usuario aluno = usuarioDao.findById(alunoId);
        if (aluno == null || !aluno.getPerfis().contains(Perfil.ALUNO)) {
             throw new IllegalArgumentException("Usuário não encontrado ou não é um aluno.");
        }
        
        // 2. Persistência
        turmaDao.matricularAluno(turmaId, alunoId);
    }
    
    /**
     * Lista todas as Turmas em que um aluno está matriculado. (Preparação para RF12)
     */
    public List<Long> listarTurmasPorAlunoId(Long alunoId) {
        return turmaDao.findTurmasByAluno(alunoId);
    }
    
    public List<Turma> listarTurmasPorProfessor(Long professorId) {
    List<Turma> todasTurmas = turmaDao.findAll(); // Busca todas as turmas
    List<Turma> turmasDoProfessor = new ArrayList<>();
    
    // Filtra as turmas onde a lista de professoresIds contém o ID do professor
    for (Turma t : todasTurmas) {
        if (t.getProfessoresIds() != null && t.getProfessoresIds().contains(professorId)) {
            turmasDoProfessor.add(t);
        }
    }
    return turmasDoProfessor;
}
    
}