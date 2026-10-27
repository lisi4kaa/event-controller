package by.vsu.util;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class DataSourceProvider {

    private static HikariDataSource dataSource;

    private DataSourceProvider (){}

    public static HikariDataSource get() {
        if (dataSource == null){
            dataSource = createDataSource();
        }
        return dataSource;
    }

    private static HikariDataSource createDataSource(){
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:postgresql://localhost:5432/event_controller");
        config.setUsername("postgres");
        config.setPassword("123");
        config.setDriverClassName("org.postgresql.Driver");
        config.setMaximumPoolSize(10);
        config.setMinimumIdle(2);
        config.setPoolName("EventControllerPool");
        return new HikariDataSource(config);
    }

    public static void close (){
        if(dataSource != null && !dataSource.isClosed()){
            dataSource.close();
        }
    }
}
