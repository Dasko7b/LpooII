package br.ufpr.avaliacao.dao.jdbc;

import br.ufpr.avaliacao.dao.Dao;
import br.ufpr.avaliacao.model.Aluno;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * DAO para a entidade Aluno, usando usuario_id como chave.
 */
public class AlunoDao implements Dao<Aluno, Long> {

    private static final Logger LOGGER = Logger.getLogger(AlunoDao.class.getName());

    @Override
    public Aluno save(Aluno aluno) {
        // PostgreSQL ON CONFLICT (UPSERT)
        String sql = "INSERT INTO ALUNO (usuario_id, matricula) VALUES (?, ?) "
                   + "ON CONFLICT (usuario_id) DO UPDATE SET matricula = EXCLUDED.matricula";
        
        if (aluno.getUsuarioId() == null) {
            throw new IllegalArgumentException("Aluno deve ter um UsuarioId para ser salvo.");
        }

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, aluno.getUsuarioId());
            stmt.setString(2, aluno.getMatricula());
            
            stmt.executeUpdate();
            return aluno;

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao salvar Aluno: " + aluno.getUsuarioId(), e);
            throw new RuntimeException("Falha na persistência de Aluno.", e);
        }
    }

    @Override
    public Aluno findById(Long usuarioId) {
        String sql = "SELECT usuario_id, matricula FROM ALUNO WHERE usuario_id = ?";
        
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, usuarioId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Aluno a = new Aluno();
                    a.setUsuarioId(rs.getLong("usuario_id"));
                    a.setMatricula(rs.getString("matricula"));
                    return a;
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao buscar Aluno por UsuarioId", e);
        }
        return null;
    }

    // Métodos findAll() e delete() foram omitidos para focar na lógica principal (upsert/find)
    @Override
    public List<Aluno> findAll() { return Collections.emptyList(); }
    @Override
    public void delete(Long id) {
        String sql = "DELETE FROM ALUNO WHERE usuario_id = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao deletar Aluno", e);
            throw new RuntimeException("Falha ao deletar Aluno.", e);
        }
    }
}
