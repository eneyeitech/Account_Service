package account;

import javax.sql.RowSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

public class H2PaymentDAO implements PaymentDAO{

    @Override
    public int insertPayment(Payment payment) {
        return 0;
    }

    @Override
    public int insertPayments(Collection<Payment> payments) {
        return 0;
    }

    @Override
    public int updatePayment(Payment payment) {
        return 0;
    }

    @Override
    public RowSet selectPaymentsRS(String email) {
        return null;
    }

    @Override
    public ArrayList selectPaymentsTO(String email) {
        return null;
    }

    @Override
    public RowSet selectPaymentsForRS(String email, String date) {
        return null;
    }

    @Override
    public ArrayList selectPaymentsForTO(String email, String date) {
        return null;
    }
}
