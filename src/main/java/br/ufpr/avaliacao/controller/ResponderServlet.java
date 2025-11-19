/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.ufpr.avaliacao.controller;

/**
 *
 * @author Pedro
 */

import br.ufpr.avaliacao.model.*;
import br.ufpr.avaliacao.repository.InMemoryDatabase;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet(urlPatterns = {"/aluno/responder"})
public class ResponderServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        Usuario u = (Usuario) req.getSession().getAttribute("usuarioLogado");
        if (u == null) { resp.sendRedirect(req.getContextPath() + "/login"); return; }

        Long formularioId = Long.valueOf(req.getParameter("id"));

        Formulario f = InMemoryDatabase.findFormulario(formularioId);
        if (f == null) { resp.sendError(HttpServletResponse.SC_NOT_FOUND); return; }

        if (!f.isAnonimo() && InMemoryDatabase.jaRespondeu(formularioId, u.getId())) {
            req.setAttribute("erro", "Você já respondeu este formulário.");
        }

        List<Questao> questoes = InMemoryDatabase.listQuestoesByFormulario(formularioId);

        // >>> Prepara alternativas por questão para o JSP
        Map<Long, List<Alternativa>> alternativasPorQuestao = new HashMap<>();
        for (Questao q : questoes) {
            if (q.getTipo() == TipoQuestao.UNICA || q.getTipo() == TipoQuestao.MULTIPLA) {
                alternativasPorQuestao.put(q.getId(),
                        InMemoryDatabase.listAlternativasByQuestao(q.getId()));
            }
        }

        req.setAttribute("form", f);
        req.setAttribute("questoes", questoes);
        req.setAttribute("alternativas", alternativasPorQuestao); // << usar no JSP
        req.setAttribute("pageTitle", "Responder: " + f.getTitulo());
        req.getRequestDispatcher("/WEB-INF/views/responder-form.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        Usuario u = (Usuario) req.getSession().getAttribute("usuarioLogado");
        if (u == null) { resp.sendRedirect(req.getContextPath() + "/login"); return; }

        Long formularioId = Long.valueOf(req.getParameter("formularioId"));
        Formulario f = InMemoryDatabase.findFormulario(formularioId);
        if (f == null) { resp.sendError(HttpServletResponse.SC_NOT_FOUND); return; }

        if (!f.isAnonimo() && InMemoryDatabase.jaRespondeu(formularioId, u.getId())) {
            resp.sendRedirect(req.getContextPath() + "/aluno?msg=ja_respondeu");
            return;
        }

        Avaliacao av = new Avaliacao();
        av.setFormularioId(formularioId);
        av.setUsuarioId(f.isAnonimo() ? null : u.getId());

        List<Questao> qs = InMemoryDatabase.listQuestoesByFormulario(formularioId);
        for (Questao q : qs) {
            Resposta r = new Resposta();
            r.setQuestaoId(q.getId());
            String param = "q_" + q.getId();

            TipoQuestao tipo = q.getTipo();
            if (tipo == TipoQuestao.ABERTA) {
                r.setTextoResposta(req.getParameter(param));
            } else if (tipo == TipoQuestao.UNICA) {
                String alt = req.getParameter(param);
                if (alt != null) r.getAlternativasIds().add(Long.valueOf(alt));
            } else { // MULTIPLA
                String[] vals = req.getParameterValues(param);
                if (vals != null) for (String v : vals) r.getAlternativasIds().add(Long.valueOf(v));
            }

            av.getRespostas().add(r);
        }

        InMemoryDatabase.salvarAvaliacao(av);
        InMemoryDatabase.registrarParticipacao(formularioId, av.getUsuarioId());

        resp.sendRedirect(req.getContextPath() + "/aluno?msg=ok");
    }
}
