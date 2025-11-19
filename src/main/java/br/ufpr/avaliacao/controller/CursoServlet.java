/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.ufpr.avaliacao.controller;

/**
 *
 * @author Pedro, Gabi
 */

import br.ufpr.avaliacao.model.Curso;
import br.ufpr.avaliacao.repository.InMemoryDatabase;
import br.ufpr.avaliacao.util.ParseUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.util.List;

@WebServlet(name="CursoServlet", urlPatterns={"/admin/cursos"})
public class CursoServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String action = req.getParameter("action");
        if ("form".equals(action)) {
            Integer id = ParseUtil.toInt(req.getParameter("id"));
            Curso c = (id != null) ? InMemoryDatabase.findCurso(id) : new Curso();
            req.setAttribute("curso", c);
            req.getRequestDispatcher("/WEB-INF/views/cursos-form.jsp").forward(req, resp);
            return;
        }

        List<Curso> cursos = InMemoryDatabase.listCursos();
        req.setAttribute("cursos", cursos);
        req.getRequestDispatcher("/WEB-INF/views/cursos-list.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String action = req.getParameter("action");
        if ("save".equals(action)) {
            Integer id = ParseUtil.toInt(req.getParameter("id"));
            String nome = req.getParameter("nome");
            String curriculo = req.getParameter("curriculo");

            Curso c = (id != null) ? InMemoryDatabase.findCurso(id) : new Curso();
            c.setNome(nome);
            c.setCurriculo(curriculo);
            InMemoryDatabase.saveCurso(c);

            resp.sendRedirect(req.getContextPath() + "/admin/cursos");
            return;
        }
        if ("delete".equals(action)) {
            Integer id = ParseUtil.toInt(req.getParameter("id"));
            if (id != null) InMemoryDatabase.deleteCurso(id);
            resp.sendRedirect(req.getContextPath() + "/admin/cursos");
        }
    }
}
