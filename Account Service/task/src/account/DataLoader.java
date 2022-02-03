package account;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DataLoader {

    private GroupRepository groupRepository;

    @Autowired
    public DataLoader(GroupRepository groupRepository) {
        this.groupRepository = groupRepository;
        createRoles();
    }

    private void createRoles() {
        try {
            groupRepository.save(new Group("ROLE_ADMINISTRATOR", "Administrator Group"));
            groupRepository.save(new Group("ROLE_USER", "User Group"));
            groupRepository.save(new Group("ROLE_ACCOUNTANT", "Accountant Group"));
            groupRepository.save(new Group("ROLE_AUDITOR", "Auditor Group"));
        } catch (Exception e) {

        }
    }
}
