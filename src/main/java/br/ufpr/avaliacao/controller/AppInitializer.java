package br.ufpr.avaliacao.controller;

import br.ufpr.avaliacao.dao.jdbc.ConnectionFactory;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import java.util.logging.Level;
import java.util.logging.Logger;


@WebListener
public class AppInitializer implements ServletContextListener {

    private static final Logger LOGGER = Logger.getLogger(AppInitializer.class.getName());

    /**
     * Este método é chamado pelo servidor web quando a aplicação é inicializada.
     */
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        LOGGER.log(Level.INFO, "Iniciando a aplicação. Configurando o banco de dados...");
        
        try {
            // Chamada crucial: Cria as tabelas e insere o usuário admin no PostgreSQL.
            ConnectionFactory.initializeDatabase();
            
            // Configurações adicionais podem vir aqui:
            // Ex: Carregar dados de currículo, logar a versão, etc.
            
        } catch (Exception e) {
            // Se a inicialização falhar, o sistema deve parar ou logar um erro grave.
            LOGGER.log(Level.SEVERE, "ERRO GRAVE: Falha ao inicializar o banco de dados PostgreSQL. A aplicação não funcionará corretamente.", e);
            // Em um sistema robusto, você poderia lançar uma RuntimeException para impedir o deploy.
            throw new RuntimeException("Falha na inicialização do DB.", e);
        }
        
        LOGGER.log(Level.INFO, "Inicialização concluída com sucesso.");
    }

    /**
     * Este método é chamado pelo servidor web quando a aplicação é encerrada.
     */
    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        LOGGER.log(Level.INFO, "Encerrando a aplicação.");
        // Lógica para fechar conexões de pool ou liberar recursos, se aplicável.
    }
}
