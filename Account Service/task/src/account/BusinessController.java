package account;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

@RestController
public class BusinessController {

    @Autowired
    SqlitePaymentDAO sqlitePaymentDAO;

    @Autowired
    UserRepository userRepo;

    @GetMapping("api/empl/payment")
    public ResponseEntity<Object> getPayrolls(@AuthenticationPrincipal UserDetailsImpl details, @RequestParam(required = false) String period) {
        if (details == null) {
            return new ResponseEntity<>(Map.of("error", "email not valid"), HttpStatus.BAD_REQUEST);
        } else {
            System.out.println("PERIOD "+period);

            User user = details.getUser();
            ArrayList<Payment> collection;
            ArrayList col = new ArrayList<>();

            if (period == null) {
                collection = sqlitePaymentDAO.selectPaymentsTO(user.getEmail());
                for (Payment p : collection) {
                    String[] dateStr = p.getPeriod().split("-");
                    int i = Integer.parseInt(dateStr[0]);
                    String monthString = new DateFormatSymbols().getMonths()[i-1];

                    long dollars = p.getSalary()/100;
                    long cents = p.getSalary() % 100;

                    col.add(Map.of(
                            "name", user.getName(),
                            "lastname", user.getLastname(),
                            "period", monthString+"-"+dateStr[1],
                            "salary", dollars + " dollar(s) " + cents + " cent(s)"
                    ));
                }
                return new ResponseEntity<>(col, HttpStatus.OK);
            }
            if (!period.matches("(0[1-9]|1[0-2])-20[0-9]{2}$")){
                return new ResponseEntity<>(Map.of("error", "Bad Request", "status", 400, "path", "/api/empl/payment"), HttpStatus.BAD_REQUEST);
            }
            collection = sqlitePaymentDAO.selectPaymentsForTO(user.getEmail(), period);
            if (collection.size() > 0) {
                Payment p = collection.get(0);
                String[] dateStr = p.getPeriod().split("-");
                int i = Integer.parseInt(dateStr[0]);
                String monthString = new DateFormatSymbols().getMonths()[i-1];
                long dollars = p.getSalary()/100;
                long cents = p.getSalary() % 100;
                return new ResponseEntity<>(  Map.of(
                        "name", user.getName(),
                        "lastname", user.getLastname(),
                        "period", monthString+"-"+dateStr[1],
                        "salary", dollars + " dollar(s) " + cents + " cent(s)"
                ), HttpStatus.OK);
            }
            return new ResponseEntity<>(  Map.of(), HttpStatus.OK);
            //return new ResponseEntity<>(Map.of("id", user.getId(), "name", user.getName(), "lastname", user.getLastname(), "email", user.getEmail()), HttpStatus.OK);
        }
    }

    @PostMapping("api/acct/payments")
    public Object addPayrolls(@RequestBody Collection<Payment> payments) {

        int i = sqlitePaymentDAO.insertPayments(payments);
        System.out.println(i);
        if (i == 1) {
            return new ResponseEntity<>(Map.of("status", "Added successfully!"), HttpStatus.OK);
        }
        return new ResponseEntity<>(Map.of("message", "payments[0].salary: Salary must be non negative!, payments[1].period: Wrong date!", "error","Bad Request","status", 400, "path", "/api/acct/payments"), HttpStatus.BAD_REQUEST);
    }

    @PutMapping("api/acct/payments")
    public Object updatePayrolls(@RequestBody Payment payment) {
        if (payment.getSalary() < 0) {
            return new ResponseEntity<>(Map.of("error", "Bad Request", "status", 400, "path", "/api/acct/payments"), HttpStatus.BAD_REQUEST);
        }
        if (!payment.getPeriod().matches("(0[1-9]|1[0-2])-20[0-9]{2}$")){
            return new ResponseEntity<>(Map.of("error", "Bad Request", "status", 400, "path", "/api/acct/payments"), HttpStatus.BAD_REQUEST);
        }
        if (!userRepo.hasUser(payment.getEmployee())) {
            return new ResponseEntity<>(Map.of("error", "Bad Request", "status", 400, "path", "/api/acct/payments"), HttpStatus.BAD_REQUEST);
        }
        int i = sqlitePaymentDAO.updatePayment(payment);
        return new ResponseEntity<>(Map.of("status", "Updated successfully!"), HttpStatus.OK);
    }
}
