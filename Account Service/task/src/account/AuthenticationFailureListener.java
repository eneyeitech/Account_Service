package account;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

@Component
public class AuthenticationFailureListener implements
        ApplicationListener<AuthenticationFailureBadCredentialsEvent> {

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private LoginAttemptService loginAttemptService;

    @Autowired
    private EventRepository eventRepo;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private LockedAccounts lockedAccounts;

    @Override
    public void onApplicationEvent(AuthenticationFailureBadCredentialsEvent e) {
        final String xfHeader = request.getHeader("X-Forwarded-For");
        Object userName = e.getAuthentication().getPrincipal();
        Object credentials = e.getAuthentication().getCredentials();
        System.out.println("Failed login using USERNAME " + userName);
        System.out.println("Failed login using PASSWORD " + credentials);
        String email = (String) userName;
        Event event = new Event();
        String path = request.getRequestURI();
        event.setSubject(email.toLowerCase());
        event.setPath(path);
        event.setAction(EventEnum.LOGIN_FAILED.name());
        event.setObject(path);
        eventRepo.save(event);
        if (!userRepo.isAdmin(email)) {
            loginAttemptService.loginFailed(email.toLowerCase());
        }
        if(loginAttemptService.isBlocked(email.toLowerCase())){
            Event event1 = new Event();
            event1.setPath(path);
            event1.setSubject(email.toLowerCase());
            event1.setObject(path);
            event1.setAction(EventEnum.BRUTE_FORCE.name());
            eventRepo.save(event1);
            Event event2 = new Event();
            event2.setPath(path);
            event2.setSubject(email.toLowerCase());
            event2.setAction(EventEnum.LOCK_USER.name());
            event2.setObject("Lock user "+email);
            eventRepo.save(event2);
            lockedAccounts.lockUser(email);
        }

        /**if (xfHeader == null) {
            loginAttemptService.loginFailed(request.getRemoteAddr());
        } else {
            loginAttemptService.loginFailed(xfHeader.split(",")[0]);
        }*/
    }
}