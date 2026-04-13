package com.microslop.factory;

import com.microslop.entity.User;
import java.time.LocalDateTime;

public abstract class UserCreator {
        
    protected abstract User instantiateUser();

    public User createUser(String name, String email, String username, String password, LocalDateTime birthDate, byte[] profilePicture) {
        
        User user = instantiateUser(); 
        
        user.setName(name);
        user.setEmail(email);
        user.setUsername(username);
        user.setPassword(password);
        user.setBirthDate(birthDate);
        
        if (profilePicture != null) {
            user.setProfilePicture(profilePicture);
        }
        
        return user;
    }

}