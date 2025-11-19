package br.ufpr.avaliacao.dao.jdbc;

import br.ufpr.avaliacao.dao.Dao;
import br.ufpr.avaliacao.model.Curso;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * DAO para a entidade Curso (RF04).
 */
public class CursoDao implements Dao<Curso, Integer> {

    private static final Logger LOGGER = Logger.getLogger(CursoDao.class.getName());
    
    private Curso mapResultSetToCurso(ResultSet rs) throws SQLException {
        Curso c = new Curso();
        c.setId(rs.getInt("id"));
        c.setNome(rs.getString("nome"));
        return c;
    }

    @Override
    public Curso save(Curso c) {
        if (c.getId() == null || c.getId() == 0) {
            return insert(c);
        } else {
            return update(c);
        }
    }

    private Curso insert(Curso c) {
        // Uso de SERIAL para INT ID: RETURNING id
        String sql = "INSERT INTO CURSO (nome) VALUES (?) RETURNING id";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, c.getNome());
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    c.setId(rs.getInt(1)); // Define o ID gerado (SERIAL)
                }
            }
            return c;

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao inserir Curso", e);
            throw new RuntimeException("Falha na persistência de Curso.", e);
        }
    }

    private Curso update(Curso c) {
        String sql = "UPDATE CURSO SET nome = ? WHERE id = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, c.getNome());
            stmt.setInt(2, c.getId());
            
            stmt.executeUpdate();
            return c;

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao atualizar Curso", e);
            throw new RuntimeException("Falha na persistência de Curso.", e);
        }
    }

    @Override
    public Curso findById(Integer id) {
        String sql = "SELECT id, nome FROM CURSO WHERE id = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return mapResultSetToCurso(rs);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao buscar Curso por ID", e);
        }
        return null;
    }

    @Override
    public List<Curso> findAll() {
        List<Curso> cursos = new ArrayList<>();
        String sql = "SELECT id, nome FROM CURSO ORDER BY nome";
        try (Connection conn = ConnectionFactory.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                cursos.add(mapResultSetToCurso(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao listar todos os Cursos", e);
            throw new RuntimeException("Falha na listagem de Cursos.", e);
        }
        return cursos;
    }

    @Override
    public void delete(Integer id) {
        String sql = "DELETE FROM CURSO WHERE id = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao deletar Curso", e);
            throw new RuntimeException("Falha ao deletar Curso (Verifique chaves estrangeiras).", e);
        }
    }
}