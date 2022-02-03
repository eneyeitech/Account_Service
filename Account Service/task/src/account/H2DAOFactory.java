package account;

// Cloudscape concrete DAO Factory implementation
import java.sql.*;

public class H2DAOFactory extends DAOFactory {
    public static final String DRIVER=
            "org.h2.Driver";
    public static final String DBURL=
            "jdbc:h2:file:../service_db";

    // method to create Cloudscape connections
    public static Connection createConnection() throws SQLException, ClassNotFoundException {
        // Use DRIVER and DBURL to create a connection
        // Recommend connection pool implementation/usage
        Class.forName(DRIVER);
        return DriverManager.getConnection (DBURL, "sa","");
    }
    public PaymentDAO getPaymentDAO() {
        // H2PaymentsDAO implements PaymentsDAO
        return new H2PaymentDAO();
    }
}
