package br.ufpr.avaliacao.controller;

import br.ufpr.avaliacao.model.Alternativa;
import br.ufpr.avaliacao.model.Avaliacao;
import br.ufpr.avaliacao.model.Formulario;
import br.ufpr.avaliacao.model.Questao;
import br.ufpr.avaliacao.model.Resposta;
import br.ufpr.avaliacao.model.Usuario;
import br.ufpr.avaliacao.service.AvaliacaoService;
import br.ufpr.avaliacao.service.FormularioService;
import br.ufpr.avaliacao.util.ParseUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@WebServlet(urlPatterns = {"/aluno/avaliar", "/aluno"})
public class ResponderServlet extends HttpServlet {

    private final FormularioService formularioService = new FormularioService();
    private final AvaliacaoService avaliacaoService = new AvaliacaoService();

    // Lógica para listar formulários disponíveis para o aluno
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        Usuario aluno = (Usuario) req.getSession().getAttribute("usuarioLogado");
        if (aluno == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }
        
        String action = req.getServletPath();
        
        if ("/aluno/avaliar".equals(action)) {
            Long formularioId = ParseUtil.parseLong(req.getParameter("formId"));
            if (formularioId == null) {
                resp.sendRedirect(req.getContextPath() + "/aluno");
                return;
            }

            // 1. Busca o formulário completo (com questões e alternativas)
            Formulario formulario = formularioService.buscarFormularioCompleto(formularioId);
            if (formulario == null) {
                req.setAttribute("erro", "Formulário não encontrado.");
                req.getRequestDispatcher("/WEB-INF/views/aluno-home.jsp").forward(req, resp);
                return;
            }
            
            // 2. Verifica se já respondeu e se pode editar (RF13, RF14)
            Avaliacao avaliacaoExistente = avaliacaoService.buscarAvaliacaoParaEdicaoOuVisualizacao(formularioId, aluno.getId());

            if (avaliacaoService.avaliacaoDao.jaRespondeu(formularioId, aluno.getId()) && avaliacaoExistente == null) {
                // Se já respondeu e é anônimo (ou não identificado), bloqueia.
                req.setAttribute("erro", "Você já respondeu a esta avaliação.");
                req.getRequestDispatcher("/WEB-INF/views/aluno-home.jsp").forward(req, resp);
                return;
            }

            req.setAttribute("formulario", formulario);
            req.setAttribute("avaliacao", avaliacaoExistente); // Usado para pré-preencher em caso de edição (RF13)
            req.setAttribute("pageTitle", "Responder: " + formulario.getTitulo());
            req.getRequestDispatcher("/WEB-INF/views/responder-form.jsp").forward(req, resp);

        } else { // Rota /aluno
            // *** MUDANÇA AQUI: Lista formulários disponíveis usando o Service ***
            List<Formulario> formularios = avaliacaoService.listarFormulariosDisponiveisParaAluno(aluno.getId());

            req.setAttribute("formulariosDisponiveis", formularios);
            req.setAttribute("pageTitle", "Minhas Avaliações");
            req.getRequestDispatcher("/WEB-INF/views/aluno-home.jsp").forward(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        Usuario aluno = (Usuario) req.getSession().getAttribute("usuarioLogado");
        if (aluno == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        Long formularioId = ParseUtil.parseLong(req.getParameter("formularioId"));
        if (formularioId == null) {
            resp.sendRedirect(req.getContextPath() + "/aluno?erro=id_invalido");
            return;
        }
        
        // Simulação da coleta de respostas
        Formulario formulario = formularioService.buscarFormularioCompleto(formularioId);
        List<Resposta> respostas = new ArrayList<>();
        
        if (formulario != null && formulario.getQuestoes() != null) {
            for (Questao q : formulario.getQuestoes()) {
                String questaoIdStr = String.valueOf(q.getId());
                
                if (q.getTipo() == TipoQuestao.ABERTA) { // RF09
                    String respostaTexto = req.getParameter("resposta_aberta_" + questaoIdStr);
                    if (q.isObrigatoria() && (respostaTexto == null || respostaTexto.isEmpty())) {
                        throw new ServletException("Questão aberta " + q.getId() + " é obrigatória.");
                    }
                    Resposta r = new Resposta();
                    r.setQuestaoId(q.getId());
                    r.setValorTexto(respostaTexto);
                    respostas.add(r);
                    
                } else { // Tipo UNICA ou MULTIPLA (RF08)
                    String[] alternativasSelecionadas = req.getParameterValues("resposta_fechada_" + questaoIdStr);
                    
                    if (q.isObrigatoria() && (alternativasSelecionadas == null || alternativasSelecionadas.length == 0)) {
                        throw new ServletException("Questão " + q.getId() + " é obrigatória.");
                    }
                    
                    if (alternativasSelecionadas != null) {
                        for (String altIdStr : alternativasSelecionadas) {
                            Long altId = ParseUtil.parseLong(altIdStr);
                            if (altId != null) {
                                Resposta r = new Resposta();
                                r.setQuestaoId(q.getId());
                                r.setAlternativaId(altId);
                                respostas.add(r);
                            }
                        }
                    }
                }
            }
        }
        
        Avaliacao avaliacao = new Avaliacao();
        avaliacao.setFormularioId(formularioId);
        avaliacao.setRespostas(respostas);
        
        try {
            // Salva a avaliação (incluindo lógica de edição/anonimato)
            avaliacaoService.salvarAvaliacao(avaliacao, aluno.getId());
            resp.sendRedirect(req.getContextPath() + "/aluno?msg=sucesso");
        } catch (Exception e) {
            // Em caso de erro (ex: já respondeu), redireciona com mensagem
            req.setAttribute("erro", e.getMessage());
            doGet(req, resp); // Retorna à tela de resposta para exibir o erro
        }
    }
}