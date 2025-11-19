package br.ufpr.avaliacao.dao.jdbc;

import br.ufpr.avaliacao.dao.Dao;
import br.ufpr.avaliacao.model.Perfil;
import br.ufpr.avaliacao.model.Usuario;
import java.sql.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class UsuarioDao implements Dao<Usuario, Long> {

    private static final Logger LOGGER = Logger.getLogger(UsuarioDao.class.getName());
    
    // Nomes das colunas da tabela USUARIO (definidas no AppInitializer/ConnectionFactory)
    private static final String TABLE_FIELDS = "id, nome, email, login, senha_hash, ativo, perfis";

    /**
     * Mapeia um ResultSet para um objeto Usuario.
     * Cuida da desserialização do campo 'perfis'.
     */
    private Usuario mapResultSetToUsuario(ResultSet rs) throws SQLException {
        Usuario u = new Usuario();
        u.setId(rs.getLong("id"));
        u.setNome(rs.getString("nome"));
        u.setEmail(rs.getString("email"));
        u.setLogin(rs.getString("login"));
        u.setSenhaHash(rs.getString("senha_hash")); // RNF02: Lembre-se de usar hash seguro em produção!
        u.setAtivo(rs.getBoolean("ativo"));
        
        // Deserialização de Perfis (ex: 'ADMIN,COORDENADOR')
        String perfisStr = rs.getString("perfis");
        if (perfisStr != null && !perfisStr.isEmpty()) {
            EnumSet<Perfil> perfis = EnumSet.noneOf(Perfil.class);
            for (String p : perfisStr.split(",")) {
                try {
                    perfis.add(Perfil.valueOf(p.trim().toUpperCase()));
                } catch (IllegalArgumentException ignored) {
                    // Ignora perfis inválidos (caso o enum mude mas o DB não)
                } 
            }
            u.setPerfis(perfis);
        } else {
             u.setPerfis(EnumSet.noneOf(Perfil.class));
        }
        
        // NOTA: As propriedades Aluno e Professor (matricula, registro) devem ser 
        // buscadas em DAOs separados (AlunoDao, ProfessorDao) se o sistema precisar delas.
        
        return u;
    }

    /**
     * Serializa o Set<Perfil> para uma String separada por vírgulas.
     */
    private String serializePerfis(Set<Perfil> perfis) {
        if (perfis == null || perfis.isEmpty()) {
            return "";
        }
        return perfis.stream()
                .map(Perfil::name)
                .collect(Collectors.joining(","));
    }
    
    // =========================================================================
    // Métodos CRUD
    // =========================================================================

    @Override
    public Usuario save(Usuario u) {
        if (u.getId() == null) {
            return insert(u);
        } else {
            return update(u);
        }
    }

    private Usuario insert(Usuario u) {
        // Uso de RETURNING id para obter o ID gerado pelo PostgreSQL (BIGSERIAL)
        String sql = "INSERT INTO USUARIO (nome, email, login, senha_hash, ativo, perfis) VALUES (?, ?, ?, ?, ?, ?) RETURNING id";
        
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, u.getNome());
            stmt.setString(2, u.getEmail());
            stmt.setString(3, u.getLogin());
            stmt.setString(4, u.getSenhaHash());
            stmt.setBoolean(5, u.isAtivo());
            stmt.setString(6, serializePerfis(u.getPerfis())); // Salva perfis como String

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    u.setId(rs.getLong(1)); // Define o ID gerado
                }
            }
            return u;

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao inserir Usuario: " + u.getLogin(), e);
            throw new RuntimeException("Falha na persistência de Usuario.", e);
        }
    }

    private Usuario update(Usuario u) {
        String sql = "UPDATE USUARIO SET nome = ?, email = ?, login = ?, senha_hash = ?, ativo = ?, perfis = ? WHERE id = ?";
        
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, u.getNome());
            stmt.setString(2, u.getEmail());
            stmt.setString(3, u.getLogin());
            stmt.setString(4, u.getSenhaHash());
            stmt.setBoolean(5, u.isAtivo());
            stmt.setString(6, serializePerfis(u.getPerfis()));
            stmt.setLong(7, u.getId());
            
            stmt.executeUpdate();
            return u;

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao atualizar Usuario: " + u.getId(), e);
            throw new RuntimeException("Falha na persistência de Usuario.", e);
        }
    }

    @Override
    public Usuario findById(Long id) {
        String sql = "SELECT " + TABLE_FIELDS + " FROM USUARIO WHERE id = ?";
        
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUsuario(rs);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao buscar Usuario por ID", e);
        }
        return null;
    }

    @Override
    public List<Usuario> findAll() {
        List<Usuario> usuarios = new ArrayList<>();
        String sql = "SELECT " + TABLE_FIELDS + " FROM USUARIO ORDER BY nome";
        
        try (Connection conn = ConnectionFactory.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                usuarios.add(mapResultSetToUsuario(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao listar todos os Usuarios", e);
            throw new RuntimeException("Falha na listagem de Usuarios.", e);
        }
        return usuarios;
    }

    @Override
    public void delete(Long id) {
        String sql = "DELETE FROM USUARIO WHERE id = ?";
        
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            stmt.executeUpdate();

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao deletar Usuario: " + id, e);
            throw new RuntimeException("Falha ao deletar Usuario (Verifique chaves estrangeiras).", e);
        }
    }

    // =========================================================================
    // Método Específico de Autenticação (RF02)
    // =========================================================================

    /**
     * Implementa a lógica de autenticação (RF02), buscando um usuário ativo 
     * com o login e senha hash fornecidos.
     */
    public Usuario findByLoginESenha(String login, String senhaHash) {
        String sql = "SELECT " + TABLE_FIELDS + " FROM USUARIO WHERE login = ? AND senha_hash = ? AND ativo = TRUE";
        
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, login);
            stmt.setString(2, senhaHash);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUsuario(rs);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro na autenticação findByLoginESenha", e);
        }
        return null;
    }
}