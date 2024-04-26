package io.dedyn.hy.watchworldshop.security;

import io.dedyn.hy.watchworldshop.entities.User;
import io.dedyn.hy.watchworldshop.security.UserDetailsImpl;
import io.dedyn.hy.watchworldshop.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserRepository userRepository;

    @Autowired
    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findFirstByEmail(email);
        if (user != null) {
            return new UserDetailsImpl(user);
        }
        throw new UsernameNotFoundException("User not found with email: " + email);
    }
}
