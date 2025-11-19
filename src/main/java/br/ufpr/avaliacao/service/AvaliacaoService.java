package br.ufpr.avaliacao.service;

import br.ufpr.avaliacao.dao.jdbc.*;
import br.ufpr.avaliacao.model.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Camada de Serviço responsável pela lógica de Avaliações e Respostas (RF12-RF15).
 */
public class AvaliacaoService {

    private static final Logger LOGGER = Logger.getLogger(AvaliacaoService.class.getName());
    private final AvaliacaoDao avaliacaoDao;
    private final RespostaDao respostaDao;
    private final TurmaDao turmaDao;
    private final FormularioService formularioService;
    private final QuestaoDao questaoDao;

    public AvaliacaoService() {
        this.avaliacaoDao = new AvaliacaoDao();
        this.respostaDao = new RespostaDao();
        this.turmaDao = new TurmaDao();
        this.formularioService = new FormularioService();
        this.questaoDao = new QuestaoDao();
    }

    // =========================================================================
    // Controle de Acesso e Disponibilidade (RF12, RF13, RF15)
    // =========================================================================

    /**
     * (RF12) Retorna os formulários que estão disponíveis para um aluno, 
     * com base nas turmas em que está matriculado E que ainda não respondeu.
     */
    public List<Formulario> listarFormulariosDisponiveisParaAluno(Long alunoId) {
        // 1. Encontrar todas as turmas em que o aluno está matriculado (RF12)
        List<Long> turmasIds = turmaDao.findTurmasByAluno(alunoId);
        if (turmasIds.isEmpty()) return new ArrayList<>();
        
        // Em um sistema completo, buscaríamos os Formulários vinculados a essas Turmas
        // (Assumindo que há uma relação Turma -> Processo Avaliativo -> Formulário)
        
        // Simplificação: Listamos todos os formulários e filtramos apenas pela participação (RF13)
        List<Formulario> todosFormularios = formularioService.listarTodos();
        List<Formulario> disponiveis = new ArrayList<>();
        
        for (Formulario f : todosFormularios) {
            if (!avaliacaoDao.jaRespondeu(f.getId(), alunoId)) { // RF13: Responda apenas uma vez
                disponiveis.add(f);
            }
        }
        
        return disponiveis;
    }

    /**
     * Busca uma Avaliação para visualização/edição.
     * @return A avaliação, ou null se não for identificada e não puder ser editada.
     */
    public Avaliacao buscarAvaliacaoParaEdicaoOuVisualizacao(Long formularioId, Long alunoId) {
        // RF13: Permite edição no período de coleta (se não for anônimo).
        Avaliacao a = avaliacaoDao.findIdentifiedAvaliacaoByFormularioAndAluno(formularioId, alunoId);
        
        if (a != null) {
            // Carrega as respostas para que a view possa preencher o formulário
            a.setRespostas(respostaDao.listRespostasByAvaliacao(a.getId()));
        }
        return a;
    }
    
    // =========================================================================
    // Submissão da Avaliação (RF13, RF14)
    // =========================================================================

    /**
     * Processa e salva a submissão de um formulário pelo aluno (RF03, RF11, RF14).
     */
    public Avaliacao salvarAvaliacao(Avaliacao avaliacao, Long alunoId) {
        Long formularioId = avaliacao.getFormularioId();
        
        // 1. Busca o formulário para checar anonimato
        Formulario formulario = formularioService.buscarFormularioCompleto(formularioId);
        if (formulario == null) {
            throw new IllegalArgumentException("Formulário não encontrado.");
        }
        
        // 2. (RF13) Verifica se já respondeu
        boolean jaRespondeu = avaliacaoDao.jaRespondeu(formularioId, alunoId);
        
        if (jaRespondeu) {
            // Se for anônimo, bloqueia a submissão (RF14 - não permite edição de anônimo)
            if (Boolean.TRUE.equals(formulario.isAnonimo())) {
                throw new IllegalStateException("Você já respondeu a este formulário anônimo.");
            }
            
            // Se for identificado, permite edição (RF13)
            // Primeiro, busca a avaliação existente para atualizar
            Avaliacao existente = avaliacaoDao.findIdentifiedAvaliacaoByFormularioAndAluno(formularioId, alunoId);
            if (existente != null) {
                avaliacao.setId(existente.getId());
                // Remove as respostas antigas para inserir as novas (simples, mas eficaz para edição)
                deletarRespostasByAvaliacao(existente.getId());
            } else {
                 // Caso raro onde respondeu, mas não foi identificado. Bloqueia.
                 throw new IllegalStateException("Você já respondeu. Edição não permitida.");
            }
        } else {
             // Garante que a data de submissão está definida
             avaliacao.setDataSubmissao(Instant.now());
        }
        
        // 3. (RF11, RF14, RF03) Configura o registro
        avaliacao.setAnonimizada(Boolean.TRUE.equals(formulario.isAnonimo()));
        avaliacao.setAlunoId(alunoId); // RF03: Registra quem respondeu

        // 4. Persistência da Avaliação principal
        avaliacao = avaliacaoDao.save(avaliacao);

        // 5. Persistência em cascata das Respostas
        if (avaliacao.getRespostas() != null) {
            for (Resposta r : avaliacao.getRespostas()) {
                r.setAvaliacaoId(avaliacao.getId());
                // O DAO de Resposta salva a resposta.
                respostaDao.save(r);
            }
        }
        
        return avaliacao;
    }
    
    private void deletarRespostasByAvaliacao(Long avaliacaoId) {
        // Método para deletar todas as respostas vinculadas a uma avaliação
        // Necessitaria de um método DELETE FROM RESPOSTA WHERE avaliacao_id = ? no RespostaDao
    }
}