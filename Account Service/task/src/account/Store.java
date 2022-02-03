package account;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Configuration
public class Store {
    private Map<String, User> userStore;

    {
        userStore = new ConcurrentHashMap<>();
        User user1 = new User("name1","lastname1", "email1@acme.com", "xxx1");
        userStore.put(user1.getEmail(), user1);
        User user2 = new User("name2","lastname2", "email2@acme.com", "xxx2");
        userStore.put(user2.getEmail(), user2);
    }

    @Bean
    public Map<String, User> getUserStore() {
        return userStore;
    }

}

