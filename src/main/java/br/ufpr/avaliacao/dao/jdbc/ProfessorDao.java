package br.ufpr.avaliacao.dao.jdbc;

import br.ufpr.avaliacao.dao.Dao;
import br.ufpr.avaliacao.model.Professor;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * DAO para a entidade Professor, usando usuario_id como chave.
 */
public class ProfessorDao implements Dao<Professor, Long> {

    private static final Logger LOGGER = Logger.getLogger(ProfessorDao.class.getName());

    @Override
    public Professor save(Professor professor) {
        // PostgreSQL ON CONFLICT (UPSERT)
        String sql = "INSERT INTO PROFESSOR (usuario_id, registro, departamento) VALUES (?, ?, ?) "
                   + "ON CONFLICT (usuario_id) DO UPDATE SET registro = EXCLUDED.registro, departamento = EXCLUDED.departamento";
        
        if (professor.getUsuarioId() == null) {
            throw new IllegalArgumentException("Professor deve ter um UsuarioId para ser salvo.");
        }

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, professor.getUsuarioId());
            stmt.setString(2, professor.getRegistro());
            stmt.setString(3, professor.getDepartamento());
            
            stmt.executeUpdate();
            return professor;

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao salvar Professor: " + professor.getUsuarioId(), e);
            throw new RuntimeException("Falha na persistência de Professor.", e);
        }
    }

    @Override
    public Professor findById(Long usuarioId) {
        String sql = "SELECT usuario_id, registro, departamento FROM PROFESSOR WHERE usuario_id = ?";
        
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, usuarioId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Professor p = new Professor();
                    p.setUsuarioId(rs.getLong("usuario_id"));
                    p.setRegistro(rs.getString("registro"));
                    p.setDepartamento(rs.getString("departamento"));
                    return p;
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao buscar Professor por UsuarioId", e);
        }
        return null;
    }

    // Métodos findAll() e delete() foram omitidos para focar na lógica principal (upsert/find)
    @Override
    public List<Professor> findAll() { return Collections.emptyList(); }
    @Override
    public void delete(Long id) {
        String sql = "DELETE FROM PROFESSOR WHERE usuario_id = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao deletar Professor", e);
            throw new RuntimeException("Falha ao deletar Professor.", e);
        }
    }
}
