package account;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import javax.sql.RowSet;
import java.sql.*;
import java.util.*;
import java.util.Date;

@Component
public class SqlitePaymentDAO implements PaymentDAO {

    @Autowired
    UserRepository userRepo;

    public SqlitePaymentDAO(){

    }

    @Override
    public int insertPayment(Payment payment) {
        String insertPaymentSQL = "INSERT INTO \"payment\" " +
                "(employee, period, salary) VALUES (?, ?, ?)";

        try (Connection con = SqliteDAOFactory.createConnection()) {

            try (PreparedStatement insertPayment = con.prepareStatement(insertPaymentSQL);) {

                    // Insert a payment
                    insertPayment.setString(1, payment.getEmployee());
                    insertPayment.setString(2, payment.getPeriod());
                    insertPayment.setLong(3, payment.getSalary());
                    insertPayment.executeUpdate();
                    System.out.println(payment + " saved");

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 1;
    }

    @Override
    public int insertPayments(Collection<Payment> payments) {
        int i = 1;
        String insertPaymentSQL = "INSERT INTO \"payment\" " +
                "(employee, period, salary) VALUES (?, ?, ?)";

        try (Connection con = SqliteDAOFactory.createConnection()) {

            // Disable auto-commit mode
            con.setAutoCommit(false);

            try (PreparedStatement insertPayment = con.prepareStatement(insertPaymentSQL);) {

                // Create a savepoint
                Savepoint savepoint = con.setSavepoint();

                Iterator iterator = payments.iterator();
                String tempDate = "";
                while (iterator.hasNext()) {
                    Payment p = (Payment) iterator.next();
                    // Insert a payment
                    insertPayment.setString(1, p.getEmployee());
                    insertPayment.setString(2, p.getPeriod());
                    insertPayment.setLong(3, p.getSalary());
                    insertPayment.executeUpdate();

                    if (!userRepo.hasUser(p.getEmployee())) {
                        i = 4;
                        con.rollback(savepoint);
                    }

                    if (p.getSalary() < 0) {
                        i = 2;
                        con.rollback(savepoint);
                    }

                    if (tempDate.equalsIgnoreCase(p.getPeriod())) {
                        i = 3;
                        con.rollback(savepoint);
                    }

                    if (!p.getPeriod().matches("(0[1-9]|1[0-2])-20[0-9]{2}$")){
                        i = 5;
                        con.rollback(savepoint);
                    }

                    tempDate = p.getPeriod();

                }



                con.commit();
            } catch (SQLException e) {
                if (con != null) {
                    try {
                        System.err.print("Transaction is being rolled back");
                        con.rollback();
                    } catch (SQLException excep) {
                        excep.printStackTrace();
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    return i;
    }

    @Override
    public int updatePayment(Payment payment) {
        String updatePaymentSQL = "UPDATE \"payment\" " +
                "SET SALARY = ? WHERE EMPLOYEE = ? AND PERIOD = ?";

        try (Connection con = SqliteDAOFactory.createConnection()) {

            try (PreparedStatement insertPayment = con.prepareStatement(updatePaymentSQL);) {

                // Insert a payment
                insertPayment.setLong(1, payment.getSalary());
                insertPayment.setString(2, payment.getEmployee());
                insertPayment.setString(3, payment.getPeriod());
                insertPayment.executeUpdate();
                System.out.println(payment + " updated");

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 1;
    }

    @Override
    public RowSet selectPaymentsRS(String email) {
        return null;
    }

    @Override
    public ArrayList selectPaymentsTO(String email) {
        ArrayList<Payment> payments = new ArrayList<>();

        String updatePaymentSQL = "SELECT * FROM \"payment\" " +
                "WHERE EMPLOYEE = ? ORDER BY PERIOD DESC";

        try (Connection con = SqliteDAOFactory.createConnection()) {

            try (PreparedStatement insertPayment = con.prepareStatement(updatePaymentSQL);) {
                System.out.println("1 query successful " + email);
                // Insert a payment
                insertPayment.setString(1, email);
                ResultSet rs = insertPayment.executeQuery();
                while (rs.next()) {
                    Payment p = new Payment();
                    p.setEmployee(rs.getString(2));
                    p.setPeriod(rs.getString(3));
                    p.setSalary(rs.getLong(4));
                    payments.add(p);
                }
                System.out.println(" query successful");

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return payments;
    }

    @Override
    public RowSet selectPaymentsForRS(String email, String date) {
        return null;
    }

    @Override
    public ArrayList selectPaymentsForTO(String email, String date) {
        ArrayList<Payment> payments = new ArrayList<>();

        String updatePaymentSQL = "SELECT * FROM \"payment\" " +
                "WHERE EMPLOYEE = ? AND PERIOD = ? ORDER BY PERIOD DESC";

        try (Connection con = SqliteDAOFactory.createConnection()) {

            try (PreparedStatement insertPayment = con.prepareStatement(updatePaymentSQL);) {
                System.out.println("2 query successful " + email);
                // Insert a payment
                insertPayment.setString(1, email);
                insertPayment.setString(2, date);
                ResultSet rs = insertPayment.executeQuery();
                while (rs.next()) {
                    Payment p = new Payment();
                    p.setEmployee(rs.getString(2));
                    p.setPeriod(rs.getString(3));
                    p.setSalary(rs.getLong(4));
                    payments.add(p);
                }
                System.out.println(" query successful");

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return payments;
    }
}
