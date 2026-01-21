package com.codingshuttle.youtube.hospitalManagement.security;

import com.codingshuttle.youtube.hospitalManagement.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


//UserEntity implements UserDetails interface
//CustomeUserDetailsService(servicelayer) implements UserDeatilsService
//USerRepository is just Repository, in which like oldschool we just Extend JpaRepository, not implement it Unlike above. Implementatio layer/code by proxy class hidden from our eys

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    //return type is UserDetails but function returning User because of polymorphism
    //User user = new User();  // User implements UserDetails
    //UserDetails userDetails = user;  // ✅ Valid - upcasting to interface
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username).orElseThrow();
    }
}
