package io.dedyn.hy.watchworldshop.services;

import io.dedyn.hy.watchworldshop.entities.Role;
import io.dedyn.hy.watchworldshop.entities.User;
import io.dedyn.hy.watchworldshop.repositories.RoleRepository;
import io.dedyn.hy.watchworldshop.repositories.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public List<Role> getRoleList() {
        return roleRepository.findAll();
    }

    public List<Role> getEmployeeRoles() {
        return roleRepository.findRolesByNameIn(List.of("Admin", "Bán hàng"));
    }

    public Role getUserRole() {
        return roleRepository.findRolesByNameIn(List.of("Khách hàng")).getFirst();
    }

    public Role getRoleByName(String name) {
        return roleRepository.findRolesByNameIn(List.of(name)).getFirst();
    }

    public List<User> findAllEmployee() {
        return userRepository.findAllEmployee();
    }

    public List<User> findAllCustomer() {
        return userRepository.findAllCustomer();
    }

    public User findById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    public User findByEmail(String email) {
        return userRepository.findFirstByEmail(email);
    }

    public User save(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public void deleteById(Long id) {
        userRepository.deleteById(id);
    }

    public Boolean isUniqueEmail(User user) {
        User dbuser = userRepository.findFirstByEmail(user.getEmail());
        if (dbuser == null) return true;
        if (user.getId() == null) return false;
        return user.getId().equals(dbuser.getId());
    }
}
