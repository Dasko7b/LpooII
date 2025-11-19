package br.ufpr.avaliacao.dao.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Classe utilitária para gerenciar a conexão JDBC com PostgreSQL.
 */
public class ConnectionFactory {

    private static final Logger LOGGER = Logger.getLogger(ConnectionFactory.class.getName());

    // **** CONFIGURAÇÃO DO POSTGRESQL ****
    private static final String JDBC_URL = "jdbc:postgresql://localhost:5432/LpooDois"; 
    private static final String USER = "postgres"; 
    private static final String PASSWORD = "98317471Mjl@"; 
    // *************************************

    /**
     * Obtém uma nova conexão com o banco de dados.
     * @return Objeto Connection.
     * @throws SQLException Se ocorrer um erro de conexão.
     */
    public static Connection getConnection() throws SQLException {
        try {
            // Opcional no JDBC moderno, mas garante que o driver esteja carregado.
            Class.forName("org.postgresql.Driver"); 
            return DriverManager.getConnection(JDBC_URL, USER, PASSWORD);
        } catch (ClassNotFoundException e) {
            LOGGER.log(Level.SEVERE, "Driver JDBC do PostgreSQL não encontrado.", e);
            throw new SQLException("Driver JDBC não encontrado. Certifique-se de que a dependência está no pom.xml.", e);
        }
    }

    /**
     * Executa o script inicial de criação de tabelas usando sintaxe PostgreSQL.
     */
    public static void initializeDatabase() {
        // Uso de BIGSERIAL e SERIAL para IDs auto-incrementais (PostgreSQL)
        String sqlScript = ""
                + "CREATE TABLE IF NOT EXISTS USUARIO (id BIGSERIAL PRIMARY KEY, nome VARCHAR(255) NOT NULL, email VARCHAR(255) UNIQUE NOT NULL, login VARCHAR(50) UNIQUE NOT NULL, senha_hash VARCHAR(255) NOT NULL, ativo BOOLEAN NOT NULL, perfis VARCHAR(255));"
                + "CREATE TABLE IF NOT EXISTS ALUNO (usuario_id BIGINT PRIMARY KEY, matricula VARCHAR(50), FOREIGN KEY (usuario_id) REFERENCES USUARIO(id));"
                + "CREATE TABLE IF NOT EXISTS PROFESSOR (usuario_id BIGINT PRIMARY KEY, registro VARCHAR(50), departamento VARCHAR(255), FOREIGN KEY (usuario_id) REFERENCES USUARIO(id));"
                + "CREATE TABLE IF NOT EXISTS CURSO (id SERIAL PRIMARY KEY, nome VARCHAR(255) UNIQUE NOT NULL);"
                + "CREATE TABLE IF NOT EXISTS DISCIPLINA (id BIGSERIAL PRIMARY KEY, nome VARCHAR(255) NOT NULL, codigo VARCHAR(50) UNIQUE NOT NULL, curso_id INT, FOREIGN KEY (curso_id) REFERENCES CURSO(id));"
                + "CREATE TABLE IF NOT EXISTS TURMA (id BIGSERIAL PRIMARY KEY, nome VARCHAR(255) NOT NULL, ano INT, disciplina_id BIGINT, FOREIGN KEY (disciplina_id) REFERENCES DISCIPLINA(id));"
                + "CREATE TABLE IF NOT EXISTS TURMA_PROFESSOR (turma_id BIGINT, professor_id BIGINT, PRIMARY KEY (turma_id, professor_id), FOREIGN KEY (turma_id) REFERENCES TURMA(id), FOREIGN KEY (professor_id) REFERENCES USUARIO(id));"
                + "CREATE TABLE IF NOT EXISTS MATRICULA_TURMA (turma_id BIGINT, aluno_id BIGINT, PRIMARY KEY (turma_id, aluno_id), FOREIGN KEY (turma_id) REFERENCES TURMA(id), FOREIGN KEY (aluno_id) REFERENCES USUARIO(id));"
                + "CREATE TABLE IF NOT EXISTS FORMULARIO (id BIGSERIAL PRIMARY KEY, titulo VARCHAR(255) NOT NULL, instrucoes VARCHAR(500), anonimo BOOLEAN NOT NULL);"
                + "CREATE TABLE IF NOT EXISTS QUESTAO (id BIGSERIAL PRIMARY KEY, formulario_id BIGINT, enunciado VARCHAR(500) NOT NULL, tipo VARCHAR(50) NOT NULL, obrigatoria BOOLEAN NOT NULL, ordem INT, FOREIGN KEY (formulario_id) REFERENCES FORMULARIO(id));"
                + "CREATE TABLE IF NOT EXISTS ALTERNATIVA (id BIGSERIAL PRIMARY KEY, questao_id BIGINT, texto VARCHAR(255) NOT NULL, peso INT, ordem INT, FOREIGN KEY (questao_id) REFERENCES QUESTAO(id));"
                + "CREATE TABLE IF NOT EXISTS AVALIACAO (id BIGSERIAL PRIMARY KEY, formulario_id BIGINT, aluno_id BIGINT, data_submissao TIMESTAMP, anonimizada BOOLEAN, FOREIGN KEY (formulario_id) REFERENCES FORMULARIO(id), FOREIGN KEY (aluno_id) REFERENCES USUARIO(id));"
                + "CREATE TABLE IF NOT EXISTS RESPOSTA (id BIGSERIAL PRIMARY KEY, avaliacao_id BIGINT, questao_id BIGINT, valor_texto VARCHAR(2000), alternativa_id BIGINT, FOREIGN KEY (avaliacao_id) REFERENCES AVALIACAO(id), FOREIGN KEY (questao_id) REFERENCES QUESTAO(id), FOREIGN KEY (alternativa_id) REFERENCES ALTERNATIVA(id));";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.executeUpdate(sqlScript);
            LOGGER.info("Esquema do banco de dados PostgreSQL inicializado com sucesso.");

            // SEED: Cria um usuário admin inicial
            if (!checkIfUserExists(conn, "admin")) {
                 // A senha está em texto claro, lembre-se de usar HASH em produção (RNF02)
                 String seedSql = "INSERT INTO USUARIO (nome, email, login, senha_hash, ativo, perfis) VALUES ('Admin', 'admin@teste.com', 'admin', 'admin', TRUE, 'ADMIN,COORDENADOR');";
                 stmt.executeUpdate(seedSql);
                 LOGGER.info("Usuário 'admin' criado na base de dados PostgreSQL.");
            }

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao inicializar o banco de dados PostgreSQL.", e);
        }
    }

    private static boolean checkIfUserExists(Connection conn, String login) throws SQLException {
        String sql = "SELECT COUNT(*) FROM USUARIO WHERE login = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, login);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }
}