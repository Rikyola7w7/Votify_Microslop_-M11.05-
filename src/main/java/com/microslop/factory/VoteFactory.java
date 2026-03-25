package com.microslop.factory;

import com.microslop.entity.Project;
import com.microslop.entity.User;
import com.microslop.entity.Vote;
import org.springframework.stereotype.Component;

/**
 * Factory para crear instancias de Vote con validación.
 */
@Component
public class VoteFactory {

    public Vote crear(User usuario, Project proyecto) {
        if (usuario == null) {
            throw new IllegalArgumentException("El voto debe estar asociado a un usuario.");
        }
        if (proyecto == null) {
            throw new IllegalArgumentException("El voto debe estar asociado a un proyecto.");
        }

        return new Vote(usuario, proyecto);
    }
}
