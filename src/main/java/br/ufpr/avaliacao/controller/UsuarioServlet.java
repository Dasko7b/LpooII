/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.ufpr.avaliacao.controller;

/**
 *
 * @author Pedro, Gabi
 */

import br.ufpr.avaliacao.model.*;
import br.ufpr.avaliacao.repository.InMemoryDatabase;
import br.ufpr.avaliacao.util.ParseUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.util.EnumSet;
import java.util.List;

@WebServlet(name="UsuarioServlet", urlPatterns={"/admin/usuarios"})
public class UsuarioServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String action = req.getParameter("action");
        if ("form".equals(action)) {
            Long id = ParseUtil.toLong(req.getParameter("id"));
            Usuario u = (id != null) ? InMemoryDatabase.findUsuario(id) : new Usuario();
            Aluno a = (id != null) ? InMemoryDatabase.findAluno(id) : null;
            Professor p = (id != null) ? InMemoryDatabase.findProfessor(id) : null;

            req.setAttribute("usuario", u);
            req.setAttribute("aluno", a);
            req.setAttribute("prof", p);
            req.getRequestDispatcher("/WEB-INF/views/usuarios-form.jsp").forward(req, resp);
            return;
        }

        // lista
        List<Usuario> usuarios = InMemoryDatabase.listUsuarios();
        req.setAttribute("usuarios", usuarios);
        req.getRequestDispatcher("/WEB-INF/views/usuarios-list.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String action = req.getParameter("action");
        if ("save".equals(action)) {
            Long id = ParseUtil.toLong(req.getParameter("id"));
            String nome = req.getParameter("nome");
            String email = req.getParameter("email");
            String login = req.getParameter("login");
            String senha = req.getParameter("senha");
            boolean ativo = ParseUtil.toBool(req.getParameter("ativo"));

            Usuario u = (id != null) ? InMemoryDatabase.findUsuario(id) : new Usuario();
            u.setNome(nome);
            u.setEmail(email);
            u.setLogin(login);
            u.setSenhaHash(senha); // etapa 1: sem hash real
            u.setAtivo(ativo);

            EnumSet<Perfil> perfis = EnumSet.noneOf(Perfil.class);
            if (ParseUtil.toBool(req.getParameter("perfilAluno"))) perfis.add(Perfil.ALUNO);
            if (ParseUtil.toBool(req.getParameter("perfilProfessor"))) perfis.add(Perfil.PROFESSOR);
            if (ParseUtil.toBool(req.getParameter("perfilCoord"))) perfis.add(Perfil.COORDENADOR);
            if (ParseUtil.toBool(req.getParameter("perfilAdmin"))) perfis.add(Perfil.ADMIN);
            u.setPerfis(perfis);

            InMemoryDatabase.saveUsuario(u);

            // extensões
            String matricula = req.getParameter("matricula");
            if (perfis.contains(Perfil.ALUNO) && matricula != null && !matricula.isBlank()) {
                InMemoryDatabase.upsertAluno(new Aluno(u.getId(), matricula));
            }
            String registro = req.getParameter("registro");
            String departamento = req.getParameter("departamento");
            if (perfis.contains(Perfil.PROFESSOR) &&
                (registro != null && !registro.isBlank() || departamento != null && !departamento.isBlank())) {
                InMemoryDatabase.upsertProfessor(new Professor(u.getId(), registro, departamento));
            }

            resp.sendRedirect(req.getContextPath() + "/admin/usuarios");
            return;
        }
        if ("delete".equals(action)) {
            Long id = ParseUtil.toLong(req.getParameter("id"));
            if (id != null) InMemoryDatabase.deleteUsuario(id);
            resp.sendRedirect(req.getContextPath() + "/admin/usuarios");
        }
    }
}
