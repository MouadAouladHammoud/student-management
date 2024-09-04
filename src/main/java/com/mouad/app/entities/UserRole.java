package com.mouad.app.entities;


import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public enum UserRole {
    // Role User => n'a aucune permission spécifique
    USER(Collections.emptySet()),

    // Role Admin
    ADMIN(
        Set.of(
            Permissions.ADMIN_READ,
            Permissions.ADMIN_UPDATE,
            Permissions.ADMIN_DELETE,
            Permissions.ADMIN_CREATE,
            Permissions.MANAGER_READ,
            Permissions.MANAGER_UPDATE,
            Permissions.MANAGER_DELETE,
            Permissions.MANAGER_CREATE
        )
    ),

    // Role Manager
    MANAGER(
        Set.of(
            Permissions.MANAGER_READ,
            Permissions.MANAGER_UPDATE,
            Permissions.MANAGER_DELETE,
            Permissions.MANAGER_CREATE
        )
    )
    ;

    public final Set<Permissions> roles;
    private UserRole(Set<Permissions> permissions) {
        this.roles = permissions;
    }

    public List<SimpleGrantedAuthority> getAuthorities() {
        // TEST: Quand je me connecte avec un compte de rôle "MANAGER"
        System.out.println("");
        System.out.println("UserRole.USER: " + UserRole.USER); // Output: UserRole.USER: USER
        System.out.println("UserRole.MANAGER: " + UserRole.MANAGER); // Output: UserRole.MANAGER: MANAGER
        System.out.println("UserRole.ADMIN: " + UserRole.ADMIN); // Output: UserRole.ADMIN: ADMIN
        System.out.println("UserRole.ADMIN.roles: " + UserRole.ADMIN.roles); // Output: UserRole.ADMIN.roles: [MANAGER_DELETE, MANAGER_READ, MANAGER_UPDATE, ADMIN_CREATE, ADMIN_UPDATE, ADMIN_DELETE, MANAGER_CREATE, ADMIN_READ]

        System.out.println("");
        // NB: Ici, il est obligatoire de mettre en place le constructeur.
        System.out.println("USER.roles: " + USER.roles); // Output: USER.USER: []
        System.out.println("MANAGER.roles: " + MANAGER.roles); // Output: USER.MANAGER: [MANAGER_UPDATE, MANAGER_DELETE, MANAGER_READ, MANAGER_CREATE]
        System.out.println("ADMIN.roles: " + ADMIN.roles); // Output: USER.ADMIN: [ADMIN_DELETE, ADMIN_UPDATE, MANAGER_READ, ADMIN_READ, MANAGER_CREATE, MANAGER_DELETE, MANAGER_UPDATE, ADMIN_CREATE]

        System.out.println("");
        // NB: Ici, il est obligatoire de mettre en place le constructeur pour accéder à "this"
        // NB: Ici, "this" fait référence à l'étudiant actuellement connecté. Il correspond à l'objet "Student" de l'étudiant connecté, qui est initialisé par la classe "Student" avec la valeur du champ "role" provenant de la table "student".
        System.out.println("this: " + this); // Output: MANAGER

        System.out.println("");
        System.out.println("this.roles: " + this.roles); // Output: this.roles: [MANAGER_UPDATE, MANAGER_CREATE, MANAGER_READ, MANAGER_DELETE]

        List<SimpleGrantedAuthority> authorities = this.roles
                .stream()
                .map(permission -> new SimpleGrantedAuthority(permission.name()))
                .collect(Collectors.toList());
        authorities.add(new SimpleGrantedAuthority("ROLE_" + this.name()));

        System.out.println("");
        System.out.println("authorities: " + authorities); // Output: authorities: [MANAGER_UPDATE, MANAGER_CREATE, MANAGER_READ, MANAGER_DELETE, ROLE_MANAGER]

        return authorities;
    }

    public enum Permissions {
        ADMIN_READ,
        ADMIN_UPDATE,
        ADMIN_DELETE,
        ADMIN_CREATE,
        MANAGER_READ,
        MANAGER_UPDATE,
        MANAGER_DELETE,
        MANAGER_CREATE
    }
}


