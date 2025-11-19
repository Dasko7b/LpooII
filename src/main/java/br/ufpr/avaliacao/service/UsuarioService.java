package br.ufpr.avaliacao.service;

import br.ufpr.avaliacao.dao.jdbc.AlunoDao; 
import br.ufpr.avaliacao.dao.jdbc.ProfessorDao; 
import br.ufpr.avaliacao.dao.jdbc.UsuarioDao;
import br.ufpr.avaliacao.model.Aluno;
import br.ufpr.avaliacao.model.Professor;
import br.ufpr.avaliacao.model.Usuario;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Camada de Serviço responsável pela lógica de negócio de Usuários (Login, Cadastro, Perfis).
 */
public class UsuarioService {

    private static final Logger LOGGER = Logger.getLogger(UsuarioService.class.getName());
    private final UsuarioDao usuarioDao;
    private final AlunoDao alunoDao; // NOVO
    private final ProfessorDao professorDao; // NOVO

    public UsuarioService() {
        this.usuarioDao = new UsuarioDao();
        this.alunoDao = new AlunoDao(); // Instanciação do DAO
        this.professorDao = new ProfessorDao(); // Instanciação do DAO
    }

    // ... (autenticar, salvarUsuario, listarTodos, buscarPorId - conforme implementado anteriormente)

    public List<Usuario> listarTodos() {
        return usuarioDao.findAll();
    }

    public Usuario buscarPorId(Long id) {
        Usuario u = usuarioDao.findById(id);
        if (u != null) {
            // Se for Aluno ou Professor, carrega os dados específicos
            if (u.getPerfis().contains(br.ufpr.avaliacao.model.Perfil.ALUNO)) {
                u.setAluno(alunoDao.findById(u.getId()));
            }
            if (u.getPerfis().contains(br.ufpr.avaliacao.model.Perfil.PROFESSOR)) {
                u.setProfessor(professorDao.findById(u.getId()));
            }
        }
        return u;
    }
    
    public Usuario salvarUsuario(Usuario u) {
        // ... (Lógica de validação e hash de senha aqui)
        return usuarioDao.save(u);
    }
    
    /**
     * Salva ou atualiza os dados específicos de Aluno (Matrícula).
     */
    public Aluno salvarAluno(Aluno a) {
        return alunoDao.save(a);
    }

    /**
     * Salva ou atualiza os dados específicos de Professor (Registro, Departamento).
     */
    public Professor salvarProfessor(Professor p) {
        return professorDao.save(p);
    }
}
