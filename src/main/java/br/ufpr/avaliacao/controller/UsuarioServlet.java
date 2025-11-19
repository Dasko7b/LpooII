package br.ufpr.avaliacao.controller;

import br.ufpr.avaliacao.model.Aluno;
import br.ufpr.avaliacao.model.Perfil;
import br.ufpr.avaliacao.model.Professor;
import br.ufpr.avaliacao.model.Usuario;
import br.ufpr.avaliacao.service.UsuarioService; 
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

@WebServlet(urlPatterns = {"/admin/usuarios", "/admin/usuarios/form"})
public class UsuarioServlet extends HttpServlet {

    private final UsuarioService usuarioService = new UsuarioService();
    // NOTA: Em um sistema completo, você precisaria de AlunoService e ProfessorService/Dao aqui

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        
        String action = req.getServletPath();
        
        if ("/admin/usuarios/form".equals(action)) {
            // Lógica para carregar o formulário de um usuário específico ou novo
            String idStr = req.getParameter("id");
            Usuario usuario = null;
            if (idStr != null) {
                try {
                    Long id = Long.parseLong(idStr);
                    // *** MUDANÇA AQUI: Busca no Service (que usa o DAO) ***
                    usuario = usuarioService.buscarPorId(id); 
                } catch (NumberFormatException ignored) {
                    // Se o ID for inválido, inicia um novo usuário (null)
                }
            }
            req.setAttribute("usuario", usuario != null ? usuario : new Usuario());
            req.setAttribute("pageTitle", usuario != null ? "Editar Usuário" : "Novo Usuário");
            req.getRequestDispatcher("/WEB-INF/views/usuarios-form.jsp").forward(req, resp);
            
        } else {
            // Lógica para listar todos os usuários
            List<Usuario> usuarios = usuarioService.listarTodos();
            
            req.setAttribute("usuarios", usuarios);
            req.setAttribute("pageTitle", "Gestão de Usuários");
            req.getRequestDispatcher("/WEB-INF/views/usuarios-list.jsp").forward(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        // 1. Coleta de dados
        String idStr = req.getParameter("id");
        String nome = req.getParameter("nome");
        String email = req.getParameter("email");
        String login = req.getParameter("login");
        String senha = req.getParameter("senha"); // Em sistemas reais, a senha seria tratada separadamente (RNF02)
        boolean ativo = "on".equals(req.getParameter("ativo"));

        String[] perfisStr = req.getParameterValues("perfis");
        Set<Perfil> perfis = EnumSet.noneOf(Perfil.class);
        if (perfisStr != null) {
            for (String p : perfisStr) {
                try {
                    perfis.add(Perfil.valueOf(p.toUpperCase()));
                } catch (IllegalArgumentException ignored) {}
            }
        }
        
        // Dados de sub-classe (Aluno/Professor)
        String matricula = req.getParameter("matricula");
        String registro = req.getParameter("registro");
        String departamento = req.getParameter("departamento");

        // 2. Criação/Atualização do Objeto
        Usuario usuario;
        if (idStr != null && !idStr.isEmpty()) {
            // Edição: carrega o usuário existente.
            usuario = usuarioService.buscarPorId(Long.parseLong(idStr));
            if (usuario == null) {
                // Erro: Usuário não encontrado
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Usuário não encontrado.");
                return;
            }
        } else {
            // Novo: cria uma nova instância
            usuario = new Usuario();
        }

        usuario.setNome(nome);
        usuario.setEmail(email);
        usuario.setLogin(login);
        usuario.setAtivo(ativo);
        usuario.setPerfis(perfis);
        
        // Atualiza senha APENAS se um novo valor for fornecido (mantém o hash existente se estiver vazio)
        if (senha != null && !senha.isEmpty()) {
            usuario.setSenhaHash(senha); // NOTE: Em produção, isso deve ser um hash (ex: BCrypt.hash(senha))
        }

        // 3. Salva o Usuário Principal
        // *** MUDANÇA AQUI: Salva no Service (que usa o DAO) ***
        usuario = usuarioService.salvarUsuario(usuario);
        
        // 4. Lógica de Aluno/Professor (RF01 - RF06)
        
        // Aluno:
        if (perfis.contains(Perfil.ALUNO) && matricula != null) {
            Aluno a = usuario.getAluno() != null ? usuario.getAluno() : new Aluno();
            a.setUsuarioId(usuario.getId());
            a.setMatricula(matricula);
            // *** Novo: Salva sub-classe. Necessita de AlunoDao. ***
            usuarioService.salvarAluno(a); 
        } 
        // Professor:
        if (perfis.contains(Perfil.PROFESSOR) && registro != null) {
            Professor p = usuario.getProfessor() != null ? usuario.getProfessor() : new Professor();
            p.setUsuarioId(usuario.getId());
            p.setRegistro(registro);
            p.setDepartamento(departamento);
            // *** Novo: Salva sub-classe. Necessita de ProfessorDao. ***
            usuarioService.salvarProfessor(p);
        }
        
        // 5. Redirecionamento
        resp.sendRedirect(req.getContextPath() + "/admin/usuarios?msg=sucesso");
    }
}