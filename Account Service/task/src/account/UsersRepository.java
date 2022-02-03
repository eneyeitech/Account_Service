package account;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;

@Component
public interface UsersRepository extends CrudRepository<User, Long> {
}
