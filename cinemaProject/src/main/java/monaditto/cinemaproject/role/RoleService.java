package monaditto.cinemaproject.role;

import jakarta.transaction.Transactional;
import monaditto.cinemaproject.user.User;
import monaditto.cinemaproject.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class RoleService {
    private final RoleRepository roleRepository;

    private final UserRepository userRepository;

    @Autowired
    public RoleService(RoleRepository roleRepository, UserRepository userRepository) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
    }

    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    public Role createRole(String roleName) {
        var role = new Role(roleName);
        roleRepository.save(role);
        return role;
    }

    public void updateRoles(User user, Set<Role> roles) {
        user.setRoles(roles);
        userRepository.save(user);
        userRepository.flush();
    }

    public void addRoleToUser(User user, Role role) {
        if (!user.getRoles().contains(role)) {
            Set<Role> newRoles = new HashSet<>(user.getRoles());
            newRoles.add(role);
            user.setRoles(newRoles);
            userRepository.save(user);
        }
    }

    public void removeRoleFromUser(User user, Role role) {
        if (user.getRoles().contains(role)) {
            Set<Role> newRoles = new HashSet<>(user.getRoles());
            newRoles.remove(role);
            user.setRoles(newRoles);
            userRepository.save(user);
        }
    }

    public Set<Role> findRolesByIds(List<Long> roleIds) {
        Set<Role> resultSet = new HashSet<>();
        for (Long id : roleIds){
            Optional<Role> optional = roleRepository.findById(id);
            if(optional.isEmpty()){
                System.out.println("ERROR: SOMEHOW THE ROLE WITH ID: " + id + " GOT HERE :o");
                continue;
            }
            resultSet.add(optional.get());
        }
        System.out.println(resultSet);
        return resultSet;
    }

    public List<Role> getUserRoles(User user){
        if (user.getRoles().isEmpty()) {
            return List.of();
        }
        return user.getRoles().stream().toList();
    }

    public List<Role> getAvailableRoles(User user){
        Set<Role> returnSet = new HashSet<>(roleRepository.findAll());
        returnSet.removeAll(user.getRoles());
        return returnSet.stream().toList();
    }
}
