package account;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class GroupRepository {
    @Autowired
    UserGroupRepository repository;

    public Group findByCode(String code) {
        System.out.println(code);
        List<Group> list = new ArrayList<>();
        repository.findAll().forEach(g -> list.add(g));
        System.out.println("size");
        Optional<Group> matchingGroup = list.stream().
                filter(g -> g.getCode().equalsIgnoreCase(code)).
                findFirst();
        Group group = null;
        System.out.println(group);
        if (matchingGroup.isPresent()){
            group = matchingGroup.get();
        }
        System.out.println(group);
        return group;
    }

    public void save(Group group) {
        Group savedGroup = repository.save(group);
        System.out.println(savedGroup + " saved.");
    }

    public boolean hasCode(String code) {
        List<Group> list = new ArrayList<>();
        repository.findAll().forEach(g -> list.add(g));
        Optional<Group> matchingGroup = list.stream().
                filter(g -> g.getCode().equalsIgnoreCase(code)).
                findFirst();
        return matchingGroup.isPresent();
    }
}
