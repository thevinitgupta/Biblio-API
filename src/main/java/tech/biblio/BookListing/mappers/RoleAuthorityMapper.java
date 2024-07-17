package tech.biblio.BookListing.mappers;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import tech.biblio.BookListing.entities.Privilege;
import tech.biblio.BookListing.entities.Role;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class RoleAuthorityMapper {
    public static Collection<SimpleGrantedAuthority> rolesToAuthority(
            Collection<Role> roles, Collection<SimpleGrantedAuthority> authorities){

        for(Role role : roles){
            authorities.addAll(roleToAuthority(role, authorities));
        }
        return new HashSet<>(authorities);
    }

    public static Collection<SimpleGrantedAuthority> roleToAuthority(
            Role role, Collection<SimpleGrantedAuthority> authorities){
        Collection<Privilege> privileges = role.getPrivileges();
        for(Privilege privilege : privileges){
            authorities.add(new SimpleGrantedAuthority(privilege.getPrivilege()));
        }
        return new HashSet<>(authorities);
    }
}
