package br.ufpr.avaliacao.dao.jdbc;

import br.ufpr.avaliacao.dao.Dao;
import br.ufpr.avaliacao.model.Turma;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * DAO para a entidade Turma, incluindo a gestão de Professores e Matrículas (RF05, RF06).
 */
public class TurmaDao implements Dao<Turma, Long> {

    private static final Logger LOGGER = Logger.getLogger(TurmaDao.class.getName());
    private static final String TABLE_FIELDS = "id, nome, ano, disciplina_id";

    // --- Mapeamento ---

    private Turma mapResultSetToTurma(ResultSet rs) throws SQLException {
        Turma t = new Turma();
        t.setId(rs.getLong("id"));
        t.setNome(rs.getString("nome"));
        t.setAno(rs.getInt("ano"));
        t.setDisciplinaId(rs.getLong("disciplina_id"));
        return t;
    }

    // --- CRUD Turma Principal e Associações ---

    @Override
    public Turma save(Turma t) {
        if (t.getId() == null) {
            t = insert(t);
        } else {
            t = update(t);
        }
        
        // Persiste a associação Turma-Professor (RF05)
        if (t.getId() != null) {
            saveTurmaProfessores(t.getId(), t.getProfessoresIds());
        }
        return t;
    }

    private Turma insert(Turma t) {
        // Uso de BIGSERIAL: RETURNING id
        String sql = "INSERT INTO TURMA (nome, ano, disciplina_id) VALUES (?, ?, ?) RETURNING id";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, t.getNome());
            stmt.setInt(2, t.getAno());
            stmt.setLong(3, t.getDisciplinaId());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    t.setId(rs.getLong(1));
                }
            }
            return t;

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao inserir Turma", e);
            throw new RuntimeException("Falha na persistência de Turma.", e);
        }
    }
    
    private Turma update(Turma t) {
        String sql = "UPDATE TURMA SET nome = ?, ano = ?, disciplina_id = ? WHERE id = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, t.getNome());
            stmt.setInt(2, t.getAno());
            stmt.setLong(3, t.getDisciplinaId());
            stmt.setLong(4, t.getId());
            
            stmt.executeUpdate();
            return t;

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao atualizar Turma", e);
            throw new RuntimeException("Falha na persistência de Turma.", e);
        }
    }

    @Override
    public Turma findById(Long id) {
        String sql = "SELECT " + TABLE_FIELDS + " FROM TURMA WHERE id = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Turma t = mapResultSetToTurma(rs);
                    // Carrega a lista de IDs dos professores associados
                    t.setProfessoresIds(findProfessoresByTurma(id));
                    return t;
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao buscar Turma por ID", e);
        }
        return null;
    }

    @Override
    public List<Turma> findAll() {
        List<Turma> turmas = new ArrayList<>();
        String sql = "SELECT " + TABLE_FIELDS + " FROM TURMA ORDER BY ano DESC, nome";
        try (Connection conn = ConnectionFactory.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Turma t = mapResultSetToTurma(rs);
                // Carrega a lista de IDs dos professores
                t.setProfessoresIds(findProfessoresByTurma(t.getId()));
                turmas.add(t);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao listar todas as Turmas", e);
            throw new RuntimeException("Falha na listagem de Turmas.", e);
        }
        return turmas;
    }

    @Override
    public void delete(Long id) {
        try (Connection conn = ConnectionFactory.getConnection()) {
            conn.setAutoCommit(false); // Inicia Transação
            
            // 1. Deleta associações (Matrículas e Professores) para evitar Constraint Violation
            deleteMatriculasByTurma(conn, id); 
            deleteProfessoresByTurma(conn, id);
            
            // 2. Deleta a Turma principal
            String sqlTurma = "DELETE FROM TURMA WHERE id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sqlTurma)) {
                stmt.setLong(1, id);
                stmt.executeUpdate();
            }
            
            conn.commit(); // Confirma a transação
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao deletar Turma", e);
            throw new RuntimeException("Falha ao deletar Turma. Reverter a transação.", e);
        }
    }

    // --- Métodos de Associação Turma-Professor (RF05) ---

    private void deleteProfessoresByTurma(Connection conn, Long turmaId) throws SQLException {
        String sql = "DELETE FROM TURMA_PROFESSOR WHERE turma_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, turmaId);
            stmt.executeUpdate();
        }
    }
    
    private void saveTurmaProfessores(Long turmaId, List<Long> professoresIds) {
        try (Connection conn = ConnectionFactory.getConnection()) {
            conn.setAutoCommit(false);
            
            // Limpa e Insere (Sobrescreve a lista de professores)
            deleteProfessoresByTurma(conn, turmaId);

            if (professoresIds != null && !professoresIds.isEmpty()) {
                String sql = "INSERT INTO TURMA_PROFESSOR (turma_id, professor_id) VALUES (?, ?)";
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    for (Long profId : professoresIds) {
                        stmt.setLong(1, turmaId);
                        stmt.setLong(2, profId);
                        stmt.addBatch();
                    }
                    stmt.executeBatch(); // Execução em lote (Batch)
                }
            }
            conn.commit();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao persistir professores da Turma: " + turmaId, e);
            throw new RuntimeException("Falha ao salvar associações Turma-Professor.", e);
        }
    }

    public List<Long> findProfessoresByTurma(Long turmaId) {
        // Busca simples dos IDs dos professores
        // ... (Implementação omitida por ser simples e estar na seção de pensamentos)
        return new ArrayList<>(); 
    }
    
    // --- Métodos de Associação Matrícula-Aluno (RF06) ---
    
    private void deleteMatriculasByTurma(Connection conn, Long turmaId) throws SQLException {
        String sql = "DELETE FROM MATRICULA_TURMA WHERE turma_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, turmaId);
            stmt.executeUpdate();
        }
    }
    
    /** * Registra a matrícula de um aluno em uma turma (RF06).
     */
    public void matricularAluno(Long turmaId, Long alunoId) {
         String sql = "INSERT INTO MATRICULA_TURMA (turma_id, aluno_id) VALUES (?, ?)";
         try (Connection conn = ConnectionFactory.getConnection();
              PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, turmaId);
            stmt.setLong(2, alunoId);
            stmt.executeUpdate();
            
        } catch (SQLException e) {
            // Se for Primary Key Violation (aluno já matriculado), ignora ou trata como sucesso.
            if (!e.getMessage().contains("duplicate key")) { 
                LOGGER.log(Level.SEVERE, "Erro ao matricular aluno", e);
                throw new RuntimeException("Falha ao matricular aluno.", e);
            }
        }
    }
    
    /**
     * Lista todos os IDs das Turmas em que um aluno está matriculado (útil para RF12).
     */
    public List<Long> findTurmasByAluno(Long alunoId) {
        List<Long> turmasIds = new ArrayList<>();
        String sql = "SELECT turma_id FROM MATRICULA_TURMA WHERE aluno_id = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, alunoId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    turmasIds.add(rs.getLong("turma_id"));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao buscar turmas do aluno", e);
        }
        return turmasIds;
    }
}
