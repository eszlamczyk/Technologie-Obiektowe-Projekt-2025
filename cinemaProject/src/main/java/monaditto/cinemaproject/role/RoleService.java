package monaditto.cinemaproject.role;

import monaditto.cinemaproject.user.User;
import monaditto.cinemaproject.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;

@Service
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

    public void addRoleToUser(User user, Role role) {
        if (!user.getRoles().contains(role)) {
            user.getRoles().add(role);
            userRepository.save(user);
        }
    }

    public void removeRoleFromUser(User user, Role role) {
        if(user.getRoles().contains(role)) {
            user.getRoles().remove(role);
            userRepository.save(user);
        }
    }
}
