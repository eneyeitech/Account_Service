package account;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
class CommandRunner implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptEncoderConfig b;

    @Override
    public void run(String... args) throws Exception {


        User user = new User();
        user.setId(88L);
        user.setName("Abdulmumin");
        user.setLastname("Abdulkarim");
        user.setEmail("user@acme.com");
        user.setPassword(b.getEncoder().encode("password"));
        System.out.println("Encoded password "+user.getPassword());
        //userRepository.save(user);
    }
}
