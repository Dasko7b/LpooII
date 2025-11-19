/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.ufpr.avaliacao.controller;

/**
 *
 * @author Pedro
 */

import br.ufpr.avaliacao.model.Usuario;
import br.ufpr.avaliacao.repository.InMemoryDatabase;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;

@WebServlet(urlPatterns = {"/aluno"})
public class AlunoHomeServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Usuario u = (Usuario) req.getSession().getAttribute("usuarioLogado");
        if (u == null) { resp.sendRedirect(req.getContextPath() + "/login"); return; }

        req.setAttribute("formularios", InMemoryDatabase.listarFormulariosDisponiveisParaUsuario(u.getId()));
        req.setAttribute("pageTitle", "Formulários disponíveis");
        req.getRequestDispatcher("/WEB-INF/views/aluno-home.jsp").forward(req, resp);
    }
}
