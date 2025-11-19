package br.ufpr.avaliacao.dao.jdbc;

import br.ufpr.avaliacao.dao.Dao;
import br.ufpr.avaliacao.model.Questao;
import br.ufpr.avaliacao.model.TipoQuestao;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * DAO para a entidade Questao (RF08, RF09, RF10).
 */
public class QuestaoDao implements Dao<Questao, Long> {

    private static final Logger LOGGER = Logger.getLogger(QuestaoDao.class.getName());
    private static final String TABLE_FIELDS = "id, formulario_id, enunciado, tipo, obrigatoria, ordem";

    private final AlternativaDao alternativaDao = new AlternativaDao();
    
    private Questao mapResultSetToQuestao(ResultSet rs) throws SQLException {
        Questao q = new Questao();
        q.setId(rs.getLong("id"));
        q.setFormularioId(rs.getLong("formulario_id"));
        q.setEnunciado(rs.getString("enunciado"));
        q.setTipo(TipoQuestao.valueOf(rs.getString("tipo")));
        q.setObrigatoria(rs.getBoolean("obrigatoria")); // RF10
        q.setOrdem(rs.getInt("ordem"));
        return q;
    }

    @Override
    public Questao save(Questao q) {
        if (q.getId() == null) {
            return insert(q);
        } else {
            return update(q);
        }
    }

    private Questao insert(Questao q) {
        String sql = "INSERT INTO QUESTAO (formulario_id, enunciado, tipo, obrigatoria, ordem) VALUES (?, ?, ?, ?, ?) RETURNING id";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, q.getFormularioId());
            stmt.setString(2, q.getEnunciado());
            stmt.setString(3, q.getTipo().name()); // Salva o nome do Enum (ABERTA, UNICA, MULTIPLA)
            stmt.setBoolean(4, q.isObrigatoria()); // RF10
            stmt.setInt(5, q.getOrdem());
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) q.setId(rs.getLong(1));
            }
            return q;

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao inserir Questao", e);
            throw new RuntimeException("Falha na persistência de Questao.", e);
        }
    }
    
    private Questao update(Questao q) {
        String sql = "UPDATE QUESTAO SET enunciado = ?, tipo = ?, obrigatoria = ?, ordem = ? WHERE id = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, q.getEnunciado());
            stmt.setString(2, q.getTipo().name());
            stmt.setBoolean(3, q.isObrigatoria());
            stmt.setInt(4, q.getOrdem());
            stmt.setLong(5, q.getId());
            
            stmt.executeUpdate();
            return q;

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao atualizar Questao", e);
            throw new RuntimeException("Falha na persistência de Questao.", e);
        }
    }

    @Override
    public Questao findById(Long id) {
        String sql = "SELECT " + TABLE_FIELDS + " FROM QUESTAO WHERE id = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Questao q = mapResultSetToQuestao(rs);
                    // Carrega Alternativas se for múltipla escolha (RF08)
                    if (q.getTipo() != TipoQuestao.ABERTA) {
                        q.setAlternativas(alternativaDao.listAlternativasByQuestao(q.getId()));
                    }
                    return q;
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao buscar Questao por ID", e);
        }
        return null;
    }

    @Override
    public List<Questao> findAll() { return Collections.emptyList(); } // Não utilizado

    @Override
    public void delete(Long id) {
        // NOTA: A exclusão deve ser em cascata: Respostas -> Alternativas -> Questão.
        throw new UnsupportedOperationException("Exclusão de questão exige deleção em cascata (Alternativas). Use o Service.");
    }

    /** Lista todas as questões de um formulário, ordenadas por ordem. */
    public List<Questao> listQuestoesByFormulario(Long formularioId) {
        List<Questao> list = new ArrayList<>();
        String sql = "SELECT " + TABLE_FIELDS + " FROM QUESTAO WHERE formulario_id = ? ORDER BY ordem";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, formularioId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Questao q = mapResultSetToQuestao(rs);
                    // Carrega Alternativas (e.g., para exibição no formulário de edição)
                    if (q.getTipo() != TipoQuestao.ABERTA) {
                        q.setAlternativas(alternativaDao.listAlternativasByQuestao(q.getId()));
                    }
                    list.add(q);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao listar questões por FormularioId", e);
        }
        return list;
    }
}
