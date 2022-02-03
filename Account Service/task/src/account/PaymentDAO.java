package account;

import javax.sql.RowSet;
import java.util.ArrayList;
import java.util.Collection;

// Interface that all CustomerDAOs must support
public interface PaymentDAO {
    public int insertPayment(Payment payment);
    public int insertPayments(Collection<Payment> payments);
    public int updatePayment(Payment payment);
    public RowSet selectPaymentsRS(String email);
    public ArrayList selectPaymentsTO(String email);
    public RowSet selectPaymentsForRS(String email, String date);
    public ArrayList selectPaymentsForTO(String email, String date);
}
