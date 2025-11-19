package br.ufpr.avaliacao.dao.jdbc;

import br.ufpr.avaliacao.dao.Dao;
import br.ufpr.avaliacao.model.Formulario;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * DAO para a entidade Formulario (RF07, RF11).
 * Não gerencia Questões e Alternativas diretamente, mas sim Formulario.
 */
public class FormularioDao implements Dao<Formulario, Long> {

    private static final Logger LOGGER = Logger.getLogger(FormularioDao.class.getName());
    private static final String TABLE_FIELDS = "id, titulo, instrucoes, anonimo";

    private Formulario mapResultSetToFormulario(ResultSet rs) throws SQLException {
        Formulario f = new Formulario();
        f.setId(rs.getLong("id"));
        f.setTitulo(rs.getString("titulo"));
        f.setInstrucoes(rs.getString("instrucoes"));
        f.setAnonimo(rs.getBoolean("anonimo"));
        // NOTA: As questões não são carregadas aqui, mas sim pelo Service/QuestaoDao,
        // para evitar loops e sobrecarga de memória (Lazy Loading implícito).
        return f;
    }

    @Override
    public Formulario save(Formulario f) {
        if (f.getId() == null) {
            return insert(f);
        } else {
            return update(f);
        }
    }

    private Formulario insert(Formulario f) {
        String sql = "INSERT INTO FORMULARIO (titulo, instrucoes, anonimo) VALUES (?, ?, ?) RETURNING id";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, f.getTitulo());
            stmt.setString(2, f.getInstrucoes());
            stmt.setBoolean(3, f.isAnonimo()); // RF11
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) f.setId(rs.getLong(1));
            }
            return f;

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao inserir Formulario", e);
            throw new RuntimeException("Falha na persistência de Formulario.", e);
        }
    }

    private Formulario update(Formulario f) {
        String sql = "UPDATE FORMULARIO SET titulo = ?, instrucoes = ?, anonimo = ? WHERE id = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, f.getTitulo());
            stmt.setString(2, f.getInstrucoes());
            stmt.setBoolean(3, f.isAnonimo()); // RF11
            stmt.setLong(4, f.getId());
            
            stmt.executeUpdate();
            return f;

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao atualizar Formulario", e);
            throw new RuntimeException("Falha na persistência de Formulario.", e);
        }
    }

    @Override
    public Formulario findById(Long id) {
        String sql = "SELECT " + TABLE_FIELDS + " FROM FORMULARIO WHERE id = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return mapResultSetToFormulario(rs);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao buscar Formulario por ID", e);
        }
        return null;
    }

    @Override
    public List<Formulario> findAll() {
        List<Formulario> formularios = new ArrayList<>();
        String sql = "SELECT " + TABLE_FIELDS + " FROM FORMULARIO ORDER BY id DESC";
        try (Connection conn = ConnectionFactory.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                formularios.add(mapResultSetToFormulario(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao listar todos os Formularios", e);
            throw new RuntimeException("Falha na listagem de Formularios.", e);
        }
        return formularios;
    }

    @Override
    public void delete(Long id) {
        // NOTA: A exclusão deve ser em cascata: Respostas -> Avaliações -> Alternativas -> Questões -> Formulário.
        throw new UnsupportedOperationException("Exclusão de formulário exige deleção em cascata (Questões e Alternativas). Use o Service.");
    }
}
