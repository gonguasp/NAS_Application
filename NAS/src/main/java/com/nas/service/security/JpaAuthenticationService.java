package com.nas.service.security;

import com.nas.persistence.dto.UserDetailsDTO;
import com.nas.persistence.model.UserView;
import com.nas.persistence.repository.UserViewRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JpaAuthenticationService implements UserDetailsService {

    @Autowired
    @NonNull
    private UserViewRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        UserView userView = userRepository.findByEmail(userName);

        if (userView == null) {
            throw new UsernameNotFoundException("User not found: " + userName);
        }

        return new UserDetailsDTO(userView);
    }

}