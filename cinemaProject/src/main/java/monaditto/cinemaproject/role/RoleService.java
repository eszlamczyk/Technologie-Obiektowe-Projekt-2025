package monaditto.cinemaproject.role;

import jakarta.transaction.Transactional;
import monaditto.cinemaproject.user.User;
import monaditto.cinemaproject.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
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

    public List<RoleDto> getAllRoles() {
        return roleRepository.findAll().stream()
                .map(RoleDto::roleToRoleDto)
                .toList();
    }

    public RoleDto createRole(String roleName) {
        Role role = new Role(roleName);
        roleRepository.save(role);
        return RoleDto.roleToRoleDto(role);
    }

    public void updateRoles(Long userId, Set<Long> rolesId) {
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isEmpty()) {
            return;
        }

        Set<Role> roles = rolesId.stream()
                .map(roleRepository::findById)
                .map(Optional::get)
                .collect(Collectors.toSet());

        User user = optionalUser.get();
        user.setRoles(roles);
        userRepository.save(user);
        userRepository.flush();
    }

    public void addRoleToUser(Long userId, Long roleId) {
        Optional<User> optionalUser = userRepository.findById(userId);
        Optional<Role> optionalRole = roleRepository.findById(roleId);

        if (optionalUser.isEmpty() || optionalRole.isEmpty()) {
            return;
        }
        User user = optionalUser.get();
        Role role = optionalRole.get();

        if (!user.getRoles().contains(role)) {
            Set<Role> newRoles = new HashSet<>(user.getRoles());
            newRoles.add(role);
            user.setRoles(newRoles);
            userRepository.save(user);
        }
    }

    public void removeRoleFromUser(Long userId, Long roleId) {
        Optional<User> optionalUser = userRepository.findById(userId);
        Optional<Role> optionalRole = roleRepository.findById(roleId);

        if (optionalUser.isEmpty() || optionalRole.isEmpty()) {
            return;
        }
        User user = optionalUser.get();
        Role role = optionalRole.get();

        if (user.getRoles().contains(role)) {
            Set<Role> newRoles = new HashSet<>(user.getRoles());
            newRoles.remove(role);
            user.setRoles(newRoles);
            userRepository.save(user);
        }
    }

    public Set<RoleDto> findRolesByIds(List<Long> roleIds) {
        Set<RoleDto> resultSet = new HashSet<>();
        for (Long id : roleIds){
            Optional<Role> optional = roleRepository.findById(id);
            if(optional.isEmpty()){
                System.out.println("ERROR: SOMEHOW THE ROLE WITH ID: " + id + " GOT HERE :o");
                continue;
            }
            resultSet.add(RoleDto.roleToRoleDto(optional.get()));
        }
        return resultSet;
    }

    public List<RoleDto> getUserRoles(Long userId){
        Optional<User> optionalUser = userRepository.findById(userId);

        return optionalUser
                .map(user -> user.getRoles().stream()
                        .map(RoleDto::roleToRoleDto)
                        .toList())
                .orElseGet(List::of);
    }

    public List<RoleDto> getAvailableRoles(Long userId){
        List<RoleDto> roles = roleRepository.findAll().stream()
                .map(RoleDto::roleToRoleDto)
                .toList();
        Set<RoleDto> returnSet = new HashSet<>(roles);
        getUserRoles(userId).forEach(returnSet::remove);
        return returnSet.stream().toList();
    }
}
