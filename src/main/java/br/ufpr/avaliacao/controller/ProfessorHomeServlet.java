package br.ufpr.avaliacao.controller;

import br.ufpr.avaliacao.model.Turma;
import br.ufpr.avaliacao.model.Usuario;
import br.ufpr.avaliacao.model.relatorio.EstatisticaQuestao;
// import br.ufpr.avaliacao.repository.InMemoryDatabase; // REMOVIDO
import br.ufpr.avaliacao.service.ContextoService; // Para buscar turmas do professor (RF05)
import br.ufpr.avaliacao.service.RelatorioService; // Para gerar relatórios (RF16-RF19)
import br.ufpr.avaliacao.util.ParseUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet(urlPatterns = {"/professor", "/professor/relatorio"})
public class ProfessorHomeServlet extends HttpServlet {

    private final ContextoService contextoService = new ContextoService();
    private final RelatorioService relatorioService = new RelatorioService();
    
    // NOTA: Para implementar RF05, você precisará de um método em ContextoService para 
    // buscar Turmas por Professor. Para fins de demonstração, assumimos que ele existe.

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        Usuario professor = (Usuario) req.getSession().getAttribute("usuarioLogado");
        if (professor == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        String action = req.getServletPath();

        if ("/professor/relatorio".equals(action)) {
            // Lógica para visualizar o relatório de uma turma/formulário

            Long formularioId = ParseUtil.parseLong(req.getParameter("formId"));
            Long turmaId = ParseUtil.parseLong(req.getParameter("turmaId")); // RF16: Filtrar por Turma
            
            if (formularioId == null || turmaId == null) {
                req.setAttribute("erro", "Turma ou Formulário inválido.");
                // Redireciona para a home se os parâmetros estiverem incompletos
                resp.sendRedirect(req.getContextPath() + "/professor"); 
                return;
            }

            // 1. Geração do Relatório Consolidado (RF16, RF17)
            // O Service já aplica a regra de que professores não veem respostas abertas (RF19)
            List<EstatisticaQuestao> relatorio = relatorioService.gerarRelatorioConsolidado(formularioId, professor);

            req.setAttribute("relatorio", relatorio);
            req.setAttribute("pageTitle", "Relatório Consolidado");
            
            req.getRequestDispatcher("/WEB-INF/views/professor-relatorio.jsp").forward(req, resp);
            
        } else { // Rota /professor
            // 1. Listar Turmas que o professor leciona (RF05)
            // *** MUDANÇA AQUI: Assume-se que este método existe no Service ***
            // List<Turma> turmas = contextoService.listarTurmasPorProfessor(professor.getId());
            List<Turma> turmas = contextoService.listarTurmas(); // Usando findAll temporariamente
            
            // 2. Lógica para carregar os formulários vinculados às turmas (RF07)
            // ...

            req.setAttribute("turmas", turmas);
            req.setAttribute("pageTitle", "Área do Professor");
            req.getRequestDispatcher("/WEB-INF/views/professor-home.jsp").forward(req, resp);
        }
    }
}