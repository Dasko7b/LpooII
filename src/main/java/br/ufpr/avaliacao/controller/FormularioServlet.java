package br.ufpr.avaliacao.controller;

import br.ufpr.avaliacao.model.Alternativa;
import br.ufpr.avaliacao.model.Formulario;
import br.ufpr.avaliacao.model.Questao;
import br.ufpr.avaliacao.model.TipoQuestao;
import br.ufpr.avaliacao.service.FormularioService; // NOVO
import br.ufpr.avaliacao.util.ParseUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@WebServlet(urlPatterns = {"/admin/formularios", "/admin/formularios/form"})
public class FormularioServlet extends HttpServlet {

    private final FormularioService formularioService = new FormularioService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        
        String action = req.getServletPath();
        
        if ("/admin/formularios/form".equals(action)) {
            // Lógica para carregar o formulário de Curso
            String idStr = req.getParameter("id");
            Formulario formulario = null;
            if (idStr != null && !idStr.isEmpty()) {
                try {
                    Long id = ParseUtil.parseLong(idStr);
                    // *** MUDANÇA AQUI: Busca o formulário completo (com questões) no Service ***
                    formulario = formularioService.buscarFormularioCompleto(id);
                } catch (NumberFormatException ignored) {}
            }
            
            req.setAttribute("formulario", formulario != null ? formulario : new Formulario());
            req.setAttribute("pageTitle", formulario != null ? "Editar Formulário" : "Novo Formulário");
            
            // Lista de tipos de questão para o JSP
            req.setAttribute("tiposQuestao", TipoQuestao.values()); 
            
            req.getRequestDispatcher("/WEB-INF/views/formularios-form.jsp").forward(req, resp);
            
        } else {
            // Lógica para listar todos os Formulários
            
            // *** MUDANÇA AQUI: Lista no Service ***
            List<Formulario> formularios = formularioService.listarTodos();
            
            req.setAttribute("formularios", formularios);
            req.setAttribute("pageTitle", "Gestão de Formulários");
            req.getRequestDispatcher("/WEB-INF/views/formularios-list.jsp").forward(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        // 1. Coleta e mapeamento de dados do Formulário
        String idStr = req.getParameter("id");
        String titulo = req.getParameter("titulo");
        String instrucoes = req.getParameter("instrucoes");
        boolean anonimo = "on".equals(req.getParameter("anonimo")); // RF11
        
        Formulario formulario = new Formulario();
        if (idStr != null && !idStr.isEmpty()) {
            formulario.setId(ParseUtil.parseLong(idStr));
        }
        formulario.setTitulo(titulo);
        formulario.setInstrucoes(instrucoes);
        formulario.setAnonimo(anonimo);

        // 2. Coleta de Questões e Alternativas (depende da sua View: formulários-form.jsp)
        // Assume-se que as questões e alternativas vêm em arrays de parâmetros numerados (Ex: enunciado[0], tipo[0], alternativa[0][0])
        
        List<Questao> questoes = new ArrayList<>();
        String[] enunciados = req.getParameterValues("enunciado");
        String[] tipos = req.getParameterValues("tipo");
        String[] obrigatorias = req.getParameterValues("obrigatoria");

        if (enunciados != null) {
            for (int i = 0; i < enunciados.length; i++) {
                if (enunciados[i] == null || enunciados[i].trim().isEmpty()) continue;

                Questao q = new Questao();
                q.setEnunciado(enunciados[i]);
                q.setTipo(TipoQuestao.valueOf(tipos[i]));
                q.setObrigatoria("on".equals(obrigatorias[i])); // RF10
                q.setOrdem(i + 1);

                // Se for Múltipla Escolha (UNICA ou MULTIPLA - RF08)
                if (q.getTipo() != TipoQuestao.ABERTA) {
                    // NOTA: A coleta de alternativas precisa ser mais sofisticada, 
                    // geralmente vinda de campos dinamizados (ex: alternativa[i] retorna um array)
                    // Para simplificar, esta implementação assume que não estamos coletando 
                    // alternativas neste ponto, pois a estrutura de array de arrays é complexa 
                    // de mapear via `getParameterValues`. O ideal é usar um DTO ou JSON.
                    
                    // Implementação básica de coleta (assumindo que a view envia as alternativas)
                    List<Alternativa> alts = new ArrayList<>();
                    String[] textosAlternativa = req.getParameterValues("alternativa_texto_" + i); 
                    String[] pesosAlternativa = req.getParameterValues("alternativa_peso_" + i); 

                    if (textosAlternativa != null) {
                        for (int j = 0; j < textosAlternativa.length; j++) {
                            if (textosAlternativa[j] == null || textosAlternativa[j].trim().isEmpty()) continue;
                            Alternativa a = new Alternativa();
                            a.setTexto(textosAlternativa[j]);
                            // O peso deve ser opcional e ser 0 se for nulo
                            a.setPeso(ParseUtil.parseInt(pesosAlternativa != null ? pesosAlternativa[j] : "0"));
                            alts.add(a);
                        }
                    }
                    q.setAlternativas(alts);
                }
                questoes.add(q);
            }
        }
        formulario.setQuestoes(questoes);
        
        try {
            // 3. Salva o Formulário Completo (em cascata)
            // *** MUDANÇA AQUI: Salva no Service ***
            formularioService.salvarFormularioCompleto(formulario);
            
            resp.sendRedirect(req.getContextPath() + "/admin/formularios?msg=sucesso");
            
        } catch (Exception e) {
            // Trata erros de persistência/validação
            req.setAttribute("erro", "Erro ao salvar formulário: " + e.getMessage());
            req.setAttribute("formulario", formulario);
            req.setAttribute("tiposQuestao", TipoQuestao.values());
            req.getRequestDispatcher("/WEB-INF/views/formularios-form.jsp").forward(req, resp);
        }
    }
}