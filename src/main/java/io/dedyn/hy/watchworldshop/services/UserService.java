package io.dedyn.hy.watchworldshop.services;

import io.dedyn.hy.watchworldshop.entities.Role;
import io.dedyn.hy.watchworldshop.entities.User;
import io.dedyn.hy.watchworldshop.repositories.RoleRepository;
import io.dedyn.hy.watchworldshop.repositories.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    private final Integer PAGE_SIZE = 10;

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

    public Page<User> findEmployeeByKeyword(String keyword, Integer page) {
        if (page > 0) page--;
        if (page < 0) page = 0;

        return userRepository.findEmployeeByKeyword(keyword, PageRequest.of(page, PAGE_SIZE, Sort.by("id").descending()));
    }

    public Page<User> findCustomerByKeyword(String keyword, Integer page) {
        if (page > 0) page--;
        if (page < 0) page = 0;

        return userRepository.findCustomerByKeyword(keyword, PageRequest.of(page, PAGE_SIZE, Sort.by("id").descending()));
    }

    public User findById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    public User findByEmail(String email) {
        return userRepository.findFirstByEmail(email);
    }

    public User save(User user) {
        if (!user.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        } else {
            userRepository.findById(user.getId()).ifPresent(dbUser -> user.setPassword(dbUser.getPassword()));
        }
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

    public Boolean verify(Long userId, String verificationCode) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) return false;
        if (user.getVerificationCode().equals(verificationCode)) {
            user.setEnabled(true);
            user.setVerificationCode(null);
            userRepository.save(user);
            return true;
        }
        return false;
    }
}
