package br.ufpr.avaliacao.dao.jdbc;

import br.ufpr.avaliacao.dao.Dao;
import br.ufpr.avaliacao.model.Alternativa;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * DAO para a entidade Alternativa.
 */
public class AlternativaDao implements Dao<Alternativa, Long> {

    private static final Logger LOGGER = Logger.getLogger(AlternativaDao.class.getName());
    private static final String TABLE_FIELDS = "id, questao_id, texto, peso, ordem";

    private Alternativa mapResultSetToAlternativa(ResultSet rs) throws SQLException {
        Alternativa a = new Alternativa();
        a.setId(rs.getLong("id"));
        a.setQuestaoId(rs.getLong("questao_id"));
        a.setTexto(rs.getString("texto"));
        a.setPeso(rs.getInt("peso"));
        a.setOrdem(rs.getInt("ordem"));
        return a;
    }

    @Override
    public Alternativa save(Alternativa a) {
        if (a.getId() == null) {
            return insert(a);
        } else {
            return update(a);
        }
    }

    private Alternativa insert(Alternativa a) {
        String sql = "INSERT INTO ALTERNATIVA (questao_id, texto, peso, ordem) VALUES (?, ?, ?, ?) RETURNING id";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, a.getQuestaoId());
            stmt.setString(2, a.getTexto());
            stmt.setInt(3, a.getPeso());
            stmt.setInt(4, a.getOrdem());
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) a.setId(rs.getLong(1));
            }
            return a;

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao inserir Alternativa", e);
            throw new RuntimeException("Falha na persistência de Alternativa.", e);
        }
    }
    
    // ... (update e findById omitidos por serem semelhantes a CursoDao)

    @Override
    public Alternativa findById(Long id) {
        String sql = "SELECT " + TABLE_FIELDS + " FROM ALTERNATIVA WHERE id = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return mapResultSetToAlternativa(rs);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao buscar Alternativa por ID", e);
        }
        return null;
    }
    
    @Override
    public List<Alternativa> findAll() { return Collections.emptyList(); } // Não necessário para este caso de uso
    
    @Override
    public void delete(Long id) {
        String sql = "DELETE FROM ALTERNATIVA WHERE id = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao deletar Alternativa", e);
            throw new RuntimeException("Falha ao deletar Alternativa.", e);
        }
    }

    /** Lista todas as alternativas de uma questão, ordenadas por ordem (como no InMemoryDatabase). */
    public List<Alternativa> listAlternativasByQuestao(Long questaoId) {
        List<Alternativa> list = new ArrayList<>();
        String sql = "SELECT " + TABLE_FIELDS + " FROM ALTERNATIVA WHERE questao_id = ? ORDER BY ordem";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, questaoId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToAlternativa(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao listar alternativas por QuestaoId", e);
        }
        return list;
    }
}
