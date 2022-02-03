package account;

// Abstract class DAO Factory
public abstract class DAOFactory {

    // List of DAO types supported by the factory
    public static final int  H2 = 1;
    public static final int SQLITE = 2;

    // There will be a method for each DAO that can be
    // created. The concrete factories will have to
    // implement these methods.
    public abstract PaymentDAO getPaymentDAO();

    public static DAOFactory getDAOFactory(
            int whichFactory) {

        switch (whichFactory) {
            case H2:
                return new H2DAOFactory();
            case SQLITE    :
                return new SqliteDAOFactory();
            default           :
                return null;
        }
    }
}
