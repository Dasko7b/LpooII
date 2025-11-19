package br.ufpr.avaliacao.dao.jdbc;

import br.ufpr.avaliacao.dao.Dao;
import br.ufpr.avaliacao.model.Resposta;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * DAO para a entidade Resposta (RF03, RF09).
 */
public class RespostaDao implements Dao<Resposta, Long> {

    private static final Logger LOGGER = Logger.getLogger(RespostaDao.class.getName());
    private static final String TABLE_FIELDS = "id, avaliacao_id, questao_id, valor_texto, alternativa_id";

    private Resposta mapResultSetToResposta(ResultSet rs) throws SQLException {
        Resposta r = new Resposta();
        r.setId(rs.getLong("id"));
        r.setAvaliacaoId(rs.getLong("avaliacao_id"));
        r.setQuestaoId(rs.getLong("questao_id"));
        r.setValorTexto(rs.getString("valor_texto")); // Para questões ABERTAS (RF09)
        
        Long altId = rs.getLong("alternativa_id");
        if (rs.wasNull()) altId = null;
        r.setAlternativaId(altId); // Para questões UNICA/MULTIPLA (RF08)
        
        return r;
    }

    @Override
    public Resposta save(Resposta r) {
        if (r.getId() == null) {
            return insert(r);
        } else {
            return update(r);
        }
    }

    private Resposta insert(Resposta r) {
        String sql = "INSERT INTO RESPOSTA (avaliacao_id, questao_id, valor_texto, alternativa_id) VALUES (?, ?, ?, ?) RETURNING id";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, r.getAvaliacaoId());
            stmt.setLong(2, r.getQuestaoId());
            stmt.setString(3, r.getValorTexto());
            
            // Tratamento para AlternativaId (pode ser nulo)
            if (r.getAlternativaId() != null) {
                stmt.setLong(4, r.getAlternativaId());
            } else {
                stmt.setNull(4, java.sql.Types.BIGINT);
            }
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) r.setId(rs.getLong(1));
            }
            return r;

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao inserir Resposta", e);
            throw new RuntimeException("Falha na persistência de Resposta.", e);
        }
    }
    
    private Resposta update(Resposta r) {
        String sql = "UPDATE RESPOSTA SET valor_texto = ?, alternativa_id = ? WHERE id = ?";
        // NOTE: Geralmente respostas não são atualizadas, apenas inseridas, exceto para RF13 (edição).
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, r.getValorTexto());
            if (r.getAlternativaId() != null) {
                stmt.setLong(2, r.getAlternativaId());
            } else {
                stmt.setNull(2, java.sql.Types.BIGINT);
            }
            stmt.setLong(3, r.getId());
            
            stmt.executeUpdate();
            return r;

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao atualizar Resposta", e);
            throw new RuntimeException("Falha na atualização de Resposta.", e);
        }
    }

    @Override
    public Resposta findById(Long id) {
        String sql = "SELECT " + TABLE_FIELDS + " FROM RESPOSTA WHERE id = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return mapResultSetToResposta(rs);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao buscar Resposta por ID", e);
        }
        return null;
    }

    @Override
    public List<Resposta> findAll() { return Collections.emptyList(); }
    @Override
    public void delete(Long id) { /* Omitido */ }
    
    /** Lista todas as respostas de uma Avaliação específica. */
    public List<Resposta> listRespostasByAvaliacao(Long avaliacaoId) {
        List<Resposta> list = new ArrayList<>();
        String sql = "SELECT " + TABLE_FIELDS + " FROM RESPOSTA WHERE avaliacao_id = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, avaliacaoId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToResposta(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao listar respostas por AvaliacaoId", e);
        }
        return list;
    }
}