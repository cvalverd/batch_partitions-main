package com.duoc.advanced;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.support.JdbcTransactionManager;
import org.springframework.jdbc.datasource.DriverManagerDataSource;


/**
 * Configuración de la fuente de datos y el manejador de transacciones para la aplicación.
 * Define una base de datos embebida H2 y el manejador de transacciones JDBC.
 */
@Configuration
public class DataSourceConfiguration {

    /**
     * Configura una base de datos embebida de tipo H2 para la aplicación.
     * Carga el script SQL "schema-all.sql" para inicializar el esquema de la base de datos.
     *
     * @return Un objeto DataSource que representa la base de datos embebida H2 configurada.
     */
    @Bean
    public DataSource dataSource() {
       DriverManagerDataSource dataSource = new DriverManagerDataSource();

            dataSource.setDriverClassName("oracle.jdbc.OracleDriver");
            dataSource.setUrl("jdbc:oracle:thin:@n72bzhzwyzgte7oh_tp?TNS_ADMIN=/Wallet_N72BZHZWYZGTE7OH");
            dataSource.setUsername("springbatch");
            dataSource.setPassword("ClaseBackend3-");
            return dataSource;
    }

    /**
     * Configura un manejador de transacciones JDBC que utiliza el DataSource proporcionado.
     * Este manejador se encargará de la gestión de transacciones para las operaciones en la base de datos.
     *
     * @param dataSource La fuente de datos que el manejador de transacciones utilizará.
     * @return Un objeto JdbcTransactionManager configurado con el DataSource.
     */
    @Bean
    public JdbcTransactionManager transactionManager(DataSource dataSource) {
        return new JdbcTransactionManager(dataSource);
    }
}
