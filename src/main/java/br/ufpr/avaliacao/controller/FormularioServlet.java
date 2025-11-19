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
import java.time.LocalDateTime;
import java.util.List;

@WebServlet(name="FormularioServlet", urlPatterns={"/admin/formularios"})
public class FormularioServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String action = req.getParameter("action");
        if ("form".equals(action)) {
            Long id = ParseUtil.toLong(req.getParameter("id"));
            Formulario f = (id != null) ? InMemoryDatabase.findFormulario(id) : new Formulario();
            req.setAttribute("formulario", f);

            List<Questao> questoes = (id != null)
                    ? InMemoryDatabase.listQuestoesByFormulario(id)
                    : List.of();
            req.setAttribute("questoes", questoes);

            req.getRequestDispatcher("/WEB-INF/views/formularios-form.jsp").forward(req, resp);
            return;
        }

        List<Formulario> forms = InMemoryDatabase.listFormularios();
        req.setAttribute("formularios", forms);
        req.getRequestDispatcher("/WEB-INF/views/formularios-list.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String action = req.getParameter("action");

        if ("saveForm".equals(action)) {
            Long id = ParseUtil.toLong(req.getParameter("id"));
            String titulo = req.getParameter("titulo");
            boolean anon = ParseUtil.toBool(req.getParameter("anonimo"));
            String instrucoes = req.getParameter("instrucoes");

            Formulario f = (id != null) ? InMemoryDatabase.findFormulario(id) : new Formulario();
            f.setTitulo(titulo);
            f.setAnonimo(anon);
            f.setInstrucoes(instrucoes);
            f.setInicioColeta(LocalDateTime.now()); // demo
            InMemoryDatabase.saveFormulario(f);

            resp.sendRedirect(req.getContextPath() + "/admin/formularios?action=form&id=" + f.getId());
            return;
        }

        if ("addQuestao".equals(action)) {
            Long formularioId = ParseUtil.toLong(req.getParameter("formularioId"));
            String enunciado = req.getParameter("enunciado");
            boolean obrigatoria = ParseUtil.toBool(req.getParameter("obrigatoria"));
            int ordem = ParseUtil.toInt(req.getParameter("ordem")) == null ? 0 : ParseUtil.toInt(req.getParameter("ordem"));
            TipoQuestao tipo = TipoQuestao.valueOf(req.getParameter("tipo"));

            Questao q = new Questao();
            q.setFormularioId(formularioId);
            q.setEnunciado(enunciado);
            q.setObrigatoria(obrigatoria);
            q.setOrdem(ordem);
            q.setTipo(tipo);
            InMemoryDatabase.saveQuestao(q);

            if (tipo != TipoQuestao.ABERTA) {
                boolean multipla = "true".equals(req.getParameter("permiteMultipla"));
                QuestaoMultipla qm = new QuestaoMultipla();
                qm.setQuestaoId(q.getId());
                qm.setPermiteMultiplaSelecao(multipla);
                InMemoryDatabase.upsertQuestaoMultipla(qm);
            }

            resp.sendRedirect(req.getContextPath() + "/admin/formularios?action=form&id=" + formularioId);
            return;
        }

        if ("addAlternativa".equals(action)) {
            Long questaoId = ParseUtil.toLong(req.getParameter("questaoId"));
            String texto = req.getParameter("texto");
            int peso = ParseUtil.toInt(req.getParameter("peso")) == null ? 1 : ParseUtil.toInt(req.getParameter("peso"));
            int ordem = ParseUtil.toInt(req.getParameter("ordem")) == null ? 0 : ParseUtil.toInt(req.getParameter("ordem"));

            Alternativa a = new Alternativa();
            a.setQuestaoId(questaoId);
            a.setTexto(texto);
            a.setPeso(peso);
            a.setOrdem(ordem);
            InMemoryDatabase.saveAlternativa(a);

            Questao q = InMemoryDatabase.QUESTOES.get(questaoId);
            resp.sendRedirect(req.getContextPath() + "/admin/formularios?action=form&id=" + q.getFormularioId());
        }
    }
}
