package br.ufpr.avaliacao.controller;

import br.ufpr.avaliacao.model.Perfil;
import br.ufpr.avaliacao.model.Usuario;
import br.ufpr.avaliacao.service.UsuarioService; 
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.util.Set;

@WebServlet(urlPatterns = {"/login"})
public class LoginServlet extends HttpServlet {

    // Instancia o Service para uso na Camada Controller
    private final UsuarioService usuarioService = new UsuarioService();

    private boolean has(Set<Perfil> perfis, Perfil p) {
        return perfis != null && perfis.contains(p);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setAttribute("pageTitle", "Entrar");
        req.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(req, resp);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String login = req.getParameter("login");
        String senha = req.getParameter("senha");

        // *** MUDANÇA CRUCIAL: Delega a autenticação ao UsuarioService ***
        // O UsuarioService chamará o UsuarioDao que se conecta ao PostgreSQL.
        Usuario u = usuarioService.autenticar(login, senha);
        
        if (u == null) {
            req.setAttribute("erro", "Login ou senha inválidos.");
            doGet(req, resp);
            return;
        }

        HttpSession session = req.getSession(true);
        session.setAttribute("usuarioLogado", u);

        Set<Perfil> perfis = (Set<Perfil>) u.getPerfis();
        boolean isAdmin = has(perfis, Perfil.ADMIN) || has(perfis, Perfil.COORDENADOR);
        boolean isProfessor = has(perfis, Perfil.PROFESSOR);

        session.setAttribute("isAdmin", isAdmin);
        session.setAttribute("isProfessor", isProfessor);

        if (isAdmin) {
            resp.sendRedirect(req.getContextPath() + "/admin/usuarios");
        } else if (isProfessor) {
            resp.sendRedirect(req.getContextPath() + "/professor");
        } else {
            resp.sendRedirect(req.getContextPath() + "/aluno");
        }
    }
}