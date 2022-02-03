package account;

// Cloudscape concrete DAO Factory implementation
import org.sqlite.SQLiteDataSource;

import java.sql.*;

public class SqliteDAOFactory extends DAOFactory {
    public static final String DRIVER=
            "COM.cloudscape.core.RmiJdbcDriver";
    public static final String DBURL=
            "jdbc:sqlite:C:/db/accounts.db";

    // method to create Cloudscape connections
    public static Connection createConnection() throws SQLException {
        // Use DRIVER and DBURL to create a connection
        // Recommend connection pool implementation/usage
        SQLiteDataSource dataSource = new SQLiteDataSource();
        dataSource.setUrl(DBURL);
        return dataSource.getConnection();
    }

    public PaymentDAO getPaymentDAO() {
        // SqlitePaymentsDAO implements PaymentsDAO
        return new SqlitePaymentDAO();
    }
}
