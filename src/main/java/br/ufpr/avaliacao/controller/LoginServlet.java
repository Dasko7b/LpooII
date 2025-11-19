/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.ufpr.avaliacao.controller;

/**
 *
 * @author Pedro, Gabi
 */

import br.ufpr.avaliacao.model.Perfil;
import br.ufpr.avaliacao.model.Usuario;
import br.ufpr.avaliacao.repository.InMemoryDatabase;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.util.Set;

@WebServlet(urlPatterns = {"/login"})
public class LoginServlet extends HttpServlet {

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

        Usuario u = InMemoryDatabase.findByLoginESenha(login, senha);
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
