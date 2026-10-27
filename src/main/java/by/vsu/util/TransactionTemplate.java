package by.vsu.util;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.function.Function;

public class TransactionTemplate {
    private DataSource dataSource;

    public TransactionTemplate(DataSource dataSource){
        this.dataSource=dataSource;
    }

    public <T> T execute(Function<Connection,T> action){
        Connection conn = null;
        try {
            conn = this.dataSource.getConnection();
            conn.setAutoCommit(false);
            T result = action.apply(conn);
            conn.commit();
            return result;
        } catch (RuntimeException e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) { e.addSuppressed(ex); }
            }
            throw e;
        } catch (Exception e) {
            if(conn != null){
                try{
                    conn.rollback();
                } catch (SQLException ex) {
                    e.addSuppressed(ex);
                }
            }
            throw new TransactionException("Ошибка транзакции: ",e);
        }
        finally {
            if(conn != null){
                try{
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
