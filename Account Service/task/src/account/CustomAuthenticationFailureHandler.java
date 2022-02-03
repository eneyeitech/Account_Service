package account;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Locale;

@Component
public class CustomAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    @Autowired
    private MessageSource messages;

    @Autowired
    private EventRepository eventRepo;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        super.onAuthenticationFailure(request, response, exception);
        String errorMessage = messages.getMessage("message.badCredentials", null, Locale.ENGLISH);

        UserDetailsImpl details = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(details != null) {
            User user = details.getUser();
            Event event = new Event();
            String path = request.getRequestURI();
            event.setSubject(user.getEmail().toLowerCase());
            event.setPath(path);
            event.setAction(EventEnum.LOGIN_FAILED.name());
            event.setObject(path);
            eventRepo.save(event);
        }

        if (exception.getMessage().equalsIgnoreCase("blocked")) {
            if(details != null) {
                User user = details.getUser();
                Event event = new Event();
                String path = request.getRequestURI();
                event.setSubject(user.getEmail().toLowerCase());
                event.setPath(path);
                event.setAction(EventEnum.BRUTE_FORCE.name());
                event.setObject(path);
                eventRepo.save(event);
            }
            errorMessage = messages.getMessage("auth.message.blocked", null, Locale.ENGLISH);
        }
    }
}
