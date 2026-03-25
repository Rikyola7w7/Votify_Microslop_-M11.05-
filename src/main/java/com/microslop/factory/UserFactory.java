package com.microslop.factory;

import com.microslop.entity.User;
import java.time.LocalDateTime;

public class UserFactory {

    public static User createStandardUser(String name, String email, String username, String password, LocalDateTime birthDate, byte[] profilePicture) {
        
        User user = new User(name, email, username, password, birthDate);
        
        if (profilePicture != null) {
            user.setProfilePicture(profilePicture);
        }
        
        return user;
    }

}