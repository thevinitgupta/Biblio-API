package tech.biblio.BookListing.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tech.biblio.BookListing.entities.Role;
import tech.biblio.BookListing.repositories.RoleRepository;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Service
public class RoleService {
    @Autowired
    private RoleRepository roleRepository;

    public Collection<Role> getRoles(String... roleStrings) {
        Set<Role> roles = new HashSet<>();
        for (String roleString : roleStrings) {
            Role role = roleRepository.findByName(roleString);
            roles.add(role);
        }
        return roles;
    }
}
