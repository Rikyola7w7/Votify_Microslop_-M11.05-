package com.microslop.factory;

import com.microslop.entity.User;

public class StandardUserCreator extends UserCreator {
    
    @Override
    protected User instantiateUser() {
        return new User(); 
    }
}