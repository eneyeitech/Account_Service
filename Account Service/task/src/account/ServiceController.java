package account;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.*;

@RestController
public class ServiceController {

    private Store store;
    @Autowired
    UserRepository userRepo;

    @Autowired
    LockedAccounts lockedAccounts;

    @Autowired
    LoginAttemptService loginAttemptService;

    @Autowired
    GroupRepository groupRepo;

    @Autowired
    EventRepository eventRepo;

    @Autowired
    public ServiceController(Store store) {
        this.store = store;
    }

    @PutMapping("api/admin/user/role")
    public Object changeUserRole(@AuthenticationPrincipal UserDetailsImpl details, @Valid @RequestBody Role role) {
        if (details == null) {
            return new ResponseEntity<>(Map.of("error", "email not valid"), HttpStatus.BAD_REQUEST);
        } else {
            String roleString = "ROLE_"+role.getRole().toUpperCase();
            if (!groupRepo.hasCode(roleString)) {
                return new ResponseEntity<>(Map.of("error", "Not Found",
                        "message", "Role not found!",
                        "path", "/api/admin/user/role",
                        "status", 404), HttpStatus.NOT_FOUND);
            }

            User user = details.getUser();

            User userToModify = userRepo.findUserByEmail(role.getUser());
            if (userToModify == null) {
                return new ResponseEntity<>(Map.of("error", "Not Found",
                        "message", "User not found!",
                        "path", "/api/admin/user/role",
                        "status", 404), HttpStatus.NOT_FOUND);
            }

            Set<Group> groups = userToModify.getUserGroups();
            Set<String> roles = new TreeSet<>();
            for (Group g: groups) {
                roles.add(g.getCode());
            }

            System.out.println("1="+roleString+" ");
            boolean foundRole = roles.contains(roleString);

            switch (role.getOperation().toLowerCase(Locale.ROOT)) {
                case "grant":
                    if (foundRole) {
                        return new ResponseEntity<>(Map.of("error", "Bad Request",
                                "message", "Role already exist!",
                                "path", "/api/admin/user/role",
                                "status", 400), HttpStatus.BAD_REQUEST);
                    }
                    if (user.getEmail().equalsIgnoreCase(role.getUser())) {
                        return new ResponseEntity<>(Map.of("error", "Bad Request",
                                "message", "The user cannot combine administrative and business roles!",
                                "path", "/api/admin/user/role",
                                "status", 400), HttpStatus.BAD_REQUEST);
                    }
                    if (roleString.equalsIgnoreCase("role_administrator")) {
                        return new ResponseEntity<>(Map.of("error", "Bad Request",
                                "message", "The user cannot combine administrative and business roles!",
                                "path", "/api/admin/user/role",
                                "status", 400), HttpStatus.BAD_REQUEST);
                    }

                    roles.add(roleString);
                    userToModify.addUserGroups(groupRepo.findByCode(roleString));
                    Event event = new Event();
                    String path = "/api/admin/user/role";
                    event.setSubject(user.getEmail().toLowerCase());
                    event.setPath(path);
                    event.setAction(EventEnum.GRANT_ROLE.name());
                    event.setObject("Grant role "+role.getRole() +" to "+userToModify.getEmail().toLowerCase());
                    eventRepo.save(event);
                    break;
                case "remove":
                    if (!foundRole) {
                        return new ResponseEntity<>(Map.of("error", "Bad Request",
                                "message", "The user does not have a role!",
                                "path", "/api/admin/user/role",
                                "status", 400), HttpStatus.BAD_REQUEST);
                    }
                    if (roles.size() == 0) {
                        return new ResponseEntity<>(Map.of("error", "Bad Request",
                                "message", "The user does not have a role!",
                                "path", "/api/admin/user/role",
                                "status", 400), HttpStatus.BAD_REQUEST);
                    }
                    if (user.getEmail().equalsIgnoreCase(role.getUser())) {
                        return new ResponseEntity<>(Map.of("error", "Bad Request",
                                "message", "Can't remove ADMINISTRATOR role!",
                                "path", "/api/admin/user/role",
                                "status", 400), HttpStatus.BAD_REQUEST);
                    }
                    if (roles.size() == 1) {
                        return new ResponseEntity<>(Map.of("error", "Bad Request",
                                "message", "The user must have at least one role!",
                                "path", "/api/admin/user/role",
                                "status", 400), HttpStatus.BAD_REQUEST);
                    }
                    roles.remove(roleString);
                    userToModify.removeUserGroups(groupRepo.findByCode(roleString));
                    Event event1 = new Event();
                    String path1 = "/api/admin/user/role";
                    event1.setSubject(user.getEmail().toLowerCase());
                    event1.setPath(path1);
                    event1.setAction(EventEnum.REMOVE_ROLE.name());
                    event1.setObject("Remove role "+role.getRole() +" from "+userToModify.getEmail().toLowerCase());
                    eventRepo.save(event1);
                    break;
                default:
            }

            User user1 = userRepo.updateRole(userToModify);
            if (user1 == null) {

            }
            Set<Group> groups1 = user1.getUserGroups();
            Set<String> roles1 = new TreeSet<>();
            for (Group g: groups1) {
                roles1.add(g.getCode());
            }

            return new ResponseEntity<>(Map.of("id", user1.getId(),
                    "name", user1.getName(),
                    "lastname", user1.getLastname(),
                    "email", user1.getEmail().toLowerCase(),
                    "roles", roles), HttpStatus.OK);
        }
    }

    @PutMapping("api/admin/user/access")
    public Object lockUnlockUser(@AuthenticationPrincipal UserDetailsImpl details, @Valid @RequestBody Key key) {
        String path = "/api/admin/user/access";
        String userEmail = key.getUser();
        Event event = new Event();
        event.setPath(path);
        //event.setAction(operation.toUpperCase(Locale.ROOT));


        if (details == null) {
            return new ResponseEntity<>(Map.of("error", "email not valid"), HttpStatus.BAD_REQUEST);
        } else {
            User user = details.getUser();
            event.setSubject(user.getEmail().toLowerCase());
            User userToModify = userRepo.findUserByEmail(key.getUser());
            if (userToModify == null) {
                return new ResponseEntity<>(Map.of("error", "Not Found",
                        "message", "User not found!",
                        "path", "/api/admin/user/access",
                        "status", 404), HttpStatus.NOT_FOUND);
            }

            switch (key.getOperation().toLowerCase(Locale.ROOT)) {
                case "lock":
                    if (user.getEmail().equalsIgnoreCase(key.getUser())) {
                        return new ResponseEntity<>(Map.of("error", "Bad Request",
                                "message", "Can't lock the ADMINISTRATOR!",
                                "path", "/api/admin/user/access",
                                "status", 400), HttpStatus.BAD_REQUEST);
                    }

                    event.setAction(EventEnum.LOCK_USER.name());
                    event.setObject("Lock user "+userEmail.toLowerCase());
                    eventRepo.save(event);
                    userToModify.setLocked(true);
                    User userLocked = userRepo.update(userToModify);
                    lockedAccounts.lockUser(userLocked.getEmail());//lock
                    return new ResponseEntity<>(Map.of(
                            "status", "User "+key.getUser().toLowerCase()+" locked!"
                    ), HttpStatus.OK);
                case "unlock":

                    event.setAction(EventEnum.UNLOCK_USER.name());
                    event.setObject("Unlock user "+userEmail.toLowerCase());
                    eventRepo.save(event);
                    loginAttemptService.reset(userEmail.toLowerCase());
                    userToModify.setLocked(false);
                    User userUnLocked = userRepo.update(userToModify);
                    lockedAccounts.unLockUser(userUnLocked.getEmail());//unlock
                    return new ResponseEntity<>(Map.of(
                            "status", "User "+key.getUser().toLowerCase()+" unlocked!"
                    ), HttpStatus.OK);
                default:
            }

            return new ResponseEntity<>(Map.of("error", "Bad Request",
                    "message", "Operation not supported!",
                    "path", "/api/admin/user/access",
                    "status", 400), HttpStatus.BAD_REQUEST);



        }
    }

    @DeleteMapping("api/admin/user/{email}")
    public Object deleteUser(@AuthenticationPrincipal UserDetailsImpl details, @PathVariable String email) {
        if (details == null) {
            return new ResponseEntity<>(Map.of("error", "email not valid"), HttpStatus.BAD_REQUEST);
        } else {
            User user = details.getUser();
            if (user.getEmail().equalsIgnoreCase(email)) {
                return new ResponseEntity<>(Map.of("error", "Bad Request",
                        "message", "Can't remove ADMINISTRATOR role!",
                        "path", "/api/admin/user/"+email,
                        "status", 400), HttpStatus.BAD_REQUEST);
            }
            User userToDelete = userRepo.findUserByEmail(email);
            if (userToDelete == null) {
                return new ResponseEntity<>(Map.of("error", "Not Found",
                        "message", "User not found!",
                        "path", "/api/admin/user/"+email,
                        "status", 404), HttpStatus.NOT_FOUND);
            }
            userRepo.delete(userToDelete);

            Event event = new Event();
            String path = "/api/admin/user";
            event.setSubject(user.getEmail().toLowerCase());
            event.setPath(path);
            event.setAction(EventEnum.DELETE_USER.name());
            event.setObject(userToDelete.getEmail().toLowerCase());
            eventRepo.save(event);
            return new ResponseEntity<>(Map.of("user", email,
                    "status", "Deleted successfully!"), HttpStatus.OK);
        }
    }

    @GetMapping("api/admin/user")
    public Object getUsers() {
        //return store.getUserStore();
        List<Object> users = new ArrayList<>();
        List<User> userList = (List<User>) userRepo.getUsers();
        for (User user: userList) {
            Set<Group> groups = user.getUserGroups();
            Set<String> roles = new TreeSet<>();
            for (Group g: groups) {
                roles.add(g.getCode());
            }
            if (roles.contains("ROLE_ADMINISTRATOR")) {
                //continue;
            }
            users.add(Map.of("id", user.getId(),
                    "name", user.getName(),
                    "lastname", user.getLastname(),
                    "email", user.getEmail().toLowerCase(),
                    "roles", roles));
        }
        //return userRepo.getUsers();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @GetMapping("api/security/events")
    public Object getEvents() {
        return eventRepo.getEvents();
    }
}
