package br.ufpr.avaliacao.controller;

import br.ufpr.avaliacao.model.Curso;
import br.ufpr.avaliacao.model.Disciplina;
// import br.ufpr.avaliacao.repository.InMemoryDatabase; // REMOVIDO
import br.ufpr.avaliacao.service.ContextoService; // NOVO: Service para Contexto Avaliativo
import br.ufpr.avaliacao.util.ParseUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet(urlPatterns = {"/admin/cursos", "/admin/cursos/form"})
public class CursoServlet extends HttpServlet {

    private final ContextoService contextoService = new ContextoService(); // Instância do Service

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        
        String action = req.getServletPath();
        
        if ("/admin/cursos/form".equals(action)) {
            // Lógica para carregar o formulário de Curso
            String idStr = req.getParameter("id");
            Curso curso = null;
            if (idStr != null && !idStr.isEmpty()) {
                try {
                    // *** MUDANÇA AQUI: Busca no Service ***
                    Integer id = ParseUtil.parseInt(idStr);
                    curso = contextoService.buscarCursoPorId(id);
                } catch (NumberFormatException ignored) {}
            }
            
            req.setAttribute("curso", curso != null ? curso : new Curso());
            req.setAttribute("pageTitle", curso != null ? "Editar Curso" : "Novo Curso");
            
            // Lógica para listar disciplinas para visualização/edição no form do curso (opcional)
            List<Disciplina> disciplinas = contextoService.listarDisciplinas();
            req.setAttribute("disciplinas", disciplinas);
            
            req.getRequestDispatcher("/WEB-INF/views/cursos-form.jsp").forward(req, resp);
            
        } else {
            // Lógica para listar todos os Cursos
            
            // *** MUDANÇA AQUI: Lista no Service ***
            List<Curso> cursos = contextoService.listarCursos();
            
            req.setAttribute("cursos", cursos);
            req.setAttribute("pageTitle", "Gestão de Cursos");
            req.getRequestDispatcher("/WEB-INF/views/cursos-list.jsp").forward(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        // 1. Coleta e mapeamento de dados
        String idStr = req.getParameter("id");
        String nome = req.getParameter("nome");
        
        Curso curso = new Curso();
        if (idStr != null && !idStr.isEmpty()) {
            curso.setId(ParseUtil.parseInt(idStr));
        }
        curso.setNome(nome);
        
        try {
            // 2. Salva o Curso
            contextoService.salvarCurso(curso);
            
            resp.sendRedirect(req.getContextPath() + "/admin/cursos?msg=sucesso");
            
        } catch (IllegalArgumentException e) {
            req.setAttribute("erro", e.getMessage());
            req.setAttribute("curso", curso);
            req.setAttribute("pageTitle", "Erro ao Salvar Curso");
            req.getRequestDispatcher("/WEB-INF/views/cursos-form.jsp").forward(req, resp);
        } catch (RuntimeException e) {
            // Trata erros de persistência (ex: nome duplicado)
            req.setAttribute("erro", "Erro de sistema ao salvar: " + e.getMessage());
            req.setAttribute("curso", curso);
            req.setAttribute("pageTitle", "Erro de Persistência");
            req.getRequestDispatcher("/WEB-INF/views/cursos-form.jsp").forward(req, resp);
        }
    }
}