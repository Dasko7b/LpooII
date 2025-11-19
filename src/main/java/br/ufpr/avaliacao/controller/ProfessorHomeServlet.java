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

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.*;

@WebServlet("/professor")
public class ProfessorHomeServlet extends HttpServlet {

    // Classe auxiliar para exibir estatísticas
    public static class DistItem {
        private String alternativa;   // texto da alternativa
        private int votos;
        private double percentual;

        public DistItem(String alternativa, int votos, double percentual) {
            this.alternativa = alternativa;
            this.votos = votos;
            this.percentual = percentual;
        }

        public String getAlternativa() { return alternativa; }
        public void setAlternativa(String alternativa) { this.alternativa = alternativa; }

        public int getVotos() { return votos; }
        public void setVotos(int votos) { this.votos = votos; }

        public double getPercentual() { return percentual; }
        public void setPercentual(double percentual) { this.percentual = percentual; }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        List<Formulario> formularios = InMemoryDatabase.listFormularios();
        Map<Long, List<Questao>> questoesPorFormulario = new HashMap<>();
        Map<Long, List<DistItem>> distQuestoes = new HashMap<>();
        Map<Long, List<Resposta>> respostasAbertas = new HashMap<>();

        for (Formulario f : formularios) {
            List<Questao> questoes = InMemoryDatabase.listQuestoesByFormulario(f.getId());
            questoesPorFormulario.put(f.getId(), questoes);

            for (Questao q : questoes) {
                if (q.getTipo() == TipoQuestao.ABERTA) {
                    // pega todas as respostas abertas dessa questão
                    List<Resposta> respostas = InMemoryDatabase.listRespostasByQuestao(q.getId());
                    respostasAbertas.put(q.getId(), respostas);
                } else {
                    // monta distribuição de alternativas
                    List<Alternativa> alternativas = InMemoryDatabase.listAlternativasByQuestao(q.getId());
                    List<Resposta> respostas = InMemoryDatabase.listRespostasByQuestao(q.getId());

                    Map<Long, Integer> contagem = new HashMap<>();
                    for (Resposta r : respostas) {
                        for (Long altId : r.getAlternativasIds()) {
                            contagem.put(altId, contagem.getOrDefault(altId, 0) + 1);
                        }
                    }

                    int total = respostas.size();
                    List<DistItem> dist = new ArrayList<>();
                    for (Alternativa a : alternativas) {
                        int votos = contagem.getOrDefault(a.getId(), 0);
                        double perc = total > 0 ? (votos * 100.0 / total) : 0.0;
                        dist.add(new DistItem(a.getTexto(), votos, perc));
                    }
                    distQuestoes.put(q.getId(), dist);
                }
            }
        }

        req.setAttribute("formularios", formularios);
        req.setAttribute("questoesPorFormulario", questoesPorFormulario);
        req.setAttribute("distQuestoes", distQuestoes);
        req.setAttribute("respostasAbertas", respostasAbertas);

        req.getRequestDispatcher("/WEB-INF/views/professor-home.jsp").forward(req, resp);
    }
}
