package account;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolationException;
import java.util.Map;

@ControllerAdvice
public class Handler {

    @Autowired
    EventRepository eventRepo;

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handle(Exception ex,
                                         HttpServletRequest request, HttpServletResponse response) {

        if (ex instanceof NullPointerException) {
            return new ResponseEntity<>(Map.of("message", "1Bad Request", "error","Bad Request","status", 400), HttpStatus.BAD_REQUEST);
        }

        if (ex instanceof HttpRequestMethodNotSupportedException) {

            //logger
            Authentication auth
                    = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null) {
                User user = ((UserDetailsImpl) auth.getPrincipal()).getUser();

                //Log event
                Event event = new Event();
                String path = request.getRequestURI();
                event.setSubject(user.getEmail().toLowerCase());
                event.setPath(path);
                event.setAction(EventEnum.ACCESS_DENIED.name());
                event.setObject(path);
                eventRepo.save(event);
            }
            //endlogger

            return new ResponseEntity<>(Map.of(
                    "message", "Access Denied!",
                    "error","Forbidden",
                    "status", 403,
                    "path", request.getRequestURI()
            ), HttpStatus.FORBIDDEN);
        }

        if (ex instanceof AccessDeniedException) {
            return new ResponseEntity<>(Map.of(
                    "message", "Access Denied!",
                    "error","Forbidden",
                    "status", 403,
                    "path", "/api/admin/user/role"
            ), HttpStatus.FORBIDDEN);
        }

        if (ex instanceof AuthenticationException) {
            return new ResponseEntity<>(Map.of(
                    "message", "Access Denied!",
                    "error","Forbidden",
                    "status", 403,
                    "path", "/api/admin/user/role"
            ), HttpStatus.FORBIDDEN);
        }

        if (ex instanceof ConstraintViolationException) {
            return new ResponseEntity<>(Map.of("message", "3Bad Request", "error","Bad Request","status", 400), HttpStatus.BAD_REQUEST);
        }

        if (ex instanceof MethodArgumentNotValidException) {
            return new ResponseEntity<>(Map.of("message", "5Bad Request", "error","Bad Request","status", 400), HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(Map.of("message", "Password length must be 12 chars minimum!",
                "error","Bad Request",
                "status", 400,
        "path", "/api/auth/changepass"), HttpStatus.BAD_REQUEST);
    }
}
