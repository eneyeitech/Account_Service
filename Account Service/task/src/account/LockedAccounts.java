package account;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class LockedAccounts {

    final private Map<String, String> accounts = new ConcurrentHashMap<>();

    public void lockUser(String email) {
        accounts.put(email.toLowerCase(), email);
    }

    public void unLockUser(String email) {
        accounts.remove(email.toLowerCase());
    }

    public boolean isLocked(String email) {
        return accounts.containsKey(email.toLowerCase());
    }
}
