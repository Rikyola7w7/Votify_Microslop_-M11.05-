package com.microslop.factory;

import com.microslop.entity.User;
import org.springframework.stereotype.Component;

@Component
public class StandardUserCreator extends UserCreator {
    
    @Override
    protected User instantiateUser() {
        return new User(); 
    }
}