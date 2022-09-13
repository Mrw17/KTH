package com.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
public class CustomUserDetailsService implements UserDetailsService {


    /**
     * This will load the user by using the username. If it is not found it throws a
     * UsernameNotFoundException.
     * @param username This is a String parameter and is the only one to the method
     * @return This returns a new UserDetailsImp(parameter)
     * @throws UsernameNotFoundException This method throws UsernameNotFoundException
     */
    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

            return null;


    }
}