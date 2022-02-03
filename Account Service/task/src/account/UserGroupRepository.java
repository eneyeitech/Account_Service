package account;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;

@Component
public interface UserGroupRepository extends CrudRepository<Group, Long> {
}
