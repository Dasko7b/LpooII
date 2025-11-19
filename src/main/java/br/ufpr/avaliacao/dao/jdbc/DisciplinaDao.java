package br.ufpr.avaliacao.dao.jdbc;

import br.ufpr.avaliacao.dao.Dao;
import br.ufpr.avaliacao.model.Disciplina;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * DAO para a entidade Disciplina (RF04).
 */
public class DisciplinaDao implements Dao<Disciplina, Long> {

    private static final Logger LOGGER = Logger.getLogger(DisciplinaDao.class.getName());
    private static final String TABLE_FIELDS = "id, nome, codigo, curso_id";

    private Disciplina mapResultSetToDisciplina(ResultSet rs) throws SQLException {
        Disciplina d = new Disciplina();
        d.setId(rs.getLong("id"));
        d.setNome(rs.getString("nome"));
        d.setCodigo(rs.getString("codigo"));
        d.setCursoId(rs.getInt("curso_id"));
        return d;
    }

    @Override
    public Disciplina save(Disciplina d) {
        if (d.getId() == null) {
            return insert(d);
        } else {
            return update(d);
        }
    }

    private Disciplina insert(Disciplina d) {
        // Uso de BIGSERIAL: RETURNING id
        String sql = "INSERT INTO DISCIPLINA (nome, codigo, curso_id) VALUES (?, ?, ?) RETURNING id";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, d.getNome());
            stmt.setString(2, d.getCodigo());
            stmt.setInt(3, d.getCursoId());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    d.setId(rs.getLong(1));
                }
            }
            return d;

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao inserir Disciplina", e);
            throw new RuntimeException("Falha na persistência de Disciplina.", e);
        }
    }

    private Disciplina update(Disciplina d) {
        String sql = "UPDATE DISCIPLINA SET nome = ?, codigo = ?, curso_id = ? WHERE id = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, d.getNome());
            stmt.setString(2, d.getCodigo());
            stmt.setInt(3, d.getCursoId());
            stmt.setLong(4, d.getId());
            
            stmt.executeUpdate();
            return d;

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao atualizar Disciplina", e);
            throw new RuntimeException("Falha na persistência de Disciplina.", e);
        }
    }

    @Override
    public Disciplina findById(Long id) {
        String sql = "SELECT " + TABLE_FIELDS + " FROM DISCIPLINA WHERE id = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return mapResultSetToDisciplina(rs);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao buscar Disciplina por ID", e);
        }
        return null;
    }

    @Override
    public List<Disciplina> findAll() {
        List<Disciplina> disciplinas = new ArrayList<>();
        String sql = "SELECT " + TABLE_FIELDS + " FROM DISCIPLINA ORDER BY nome";
        try (Connection conn = ConnectionFactory.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                disciplinas.add(mapResultSetToDisciplina(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao listar todas as Disciplinas", e);
            throw new RuntimeException("Falha na listagem de Disciplinas.", e);
        }
        return disciplinas;
    }

    @Override
    public void delete(Long id) {
        String sql = "DELETE FROM DISCIPLINA WHERE id = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            stmt.executeUpdate();

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao deletar Disciplina", e);
            throw new RuntimeException("Falha ao deletar Disciplina (Verifique chaves estrangeiras).", e);
        }
    }
}
