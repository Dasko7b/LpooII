package br.ufpr.avaliacao.dao.jdbc;

import br.ufpr.avaliacao.dao.Dao;
import br.ufpr.avaliacao.model.Avaliacao;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * DAO para a entidade Avaliacao (RF03, RF11, RF13, RF14).
 */
public class AvaliacaoDao implements Dao<Avaliacao, Long> {

    private static final Logger LOGGER = Logger.getLogger(AvaliacaoDao.class.getName());
    private static final String TABLE_FIELDS = "id, formulario_id, aluno_id, data_submissao, anonimizada";
    
    // NOTE: O AvaliacaoDao não carrega o Formulario nem as Respostas. Isso é feito pelo Service.

    private Avaliacao mapResultSetToAvaliacao(ResultSet rs) throws SQLException {
        Avaliacao a = new Avaliacao();
        a.setId(rs.getLong("id"));
        a.setFormularioId(rs.getLong("formulario_id"));
        a.setAlunoId(rs.getLong("aluno_id")); // RF03: registra quem respondeu
        a.setDataSubmissao(rs.getTimestamp("data_submissao").toInstant());
        a.setAnonimizada(rs.getBoolean("anonimizada")); // RF11, RF14
        return a;
    }

    @Override
    public Avaliacao save(Avaliacao a) {
        if (a.getId() == null) {
            return insert(a);
        } else {
            return update(a);
        }
    }

    private Avaliacao insert(Avaliacao a) {
        String sql = "INSERT INTO AVALIACAO (formulario_id, aluno_id, data_submissao, anonimizada) VALUES (?, ?, ?, ?) RETURNING id";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, a.getFormularioId());
            stmt.setLong(2, a.getAlunoId());
            // PostgreSQL usa java.sql.Timestamp
            stmt.setTimestamp(3, a.getDataSubmissao() != null ? Timestamp.from(a.getDataSubmissao()) : new Timestamp(System.currentTimeMillis())); 
            stmt.setBoolean(4, a.isAnonimizada());
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) a.setId(rs.getLong(1));
            }
            return a;

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao inserir Avaliacao", e);
            throw new RuntimeException("Falha na persistência de Avaliacao.", e);
        }
    }

    private Avaliacao update(Avaliacao a) {
        // Atualiza apenas a data de submissão e o status de anonimato/identificação (se necessário)
        String sql = "UPDATE AVALIACAO SET data_submissao = ?, anonimizada = ? WHERE id = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setTimestamp(1, a.getDataSubmissao() != null ? Timestamp.from(a.getDataSubmissao()) : new Timestamp(System.currentTimeMillis()));
            stmt.setBoolean(2, a.isAnonimizada());
            stmt.setLong(3, a.getId());
            
            stmt.executeUpdate();
            return a;

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao atualizar Avaliacao", e);
            throw new RuntimeException("Falha na atualização de Avaliacao.", e);
        }
    }

    @Override
    public Avaliacao findById(Long id) {
        String sql = "SELECT " + TABLE_FIELDS + " FROM AVALIACAO WHERE id = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return mapResultSetToAvaliacao(rs);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao buscar Avaliacao por ID", e);
        }
        return null;
    }

    @Override
    public List<Avaliacao> findAll() { return new ArrayList<>(); }
    @Override
    public void delete(Long id) { /* Omitido */ }
    
    /**
     * Verifica se um aluno já respondeu a um determinado formulário (RF13).
     */
    public boolean jaRespondeu(Long formularioId, Long alunoId) {
        String sql = "SELECT COUNT(*) FROM AVALIACAO WHERE formulario_id = ? AND aluno_id = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, formularioId);
            stmt.setLong(2, alunoId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao verificar participação", e);
        }
        return false;
    }
    
    /** * Retorna a avaliação (ou null se anônima ou não existir) para fins de edição (RF13). 
     */
    public Avaliacao findIdentifiedAvaliacaoByFormularioAndAluno(Long formularioId, Long alunoId) {
        String sql = "SELECT " + TABLE_FIELDS + " FROM AVALIACAO WHERE formulario_id = ? AND aluno_id = ? AND anonimizada = FALSE";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, formularioId);
            stmt.setLong(2, alunoId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return mapResultSetToAvaliacao(rs);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao buscar Avaliacao identificada", e);
        }
        return null;
    }
}
