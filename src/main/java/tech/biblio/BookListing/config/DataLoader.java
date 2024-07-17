package tech.biblio.BookListing.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import tech.biblio.BookListing.entities.Privilege;
import tech.biblio.BookListing.entities.Role;
import tech.biblio.BookListing.repositories.RoleRepository;

import java.util.Set;

@Component
public class DataLoader implements CommandLineRunner {

    @Autowired
    public RoleRepository roleRepository;
    @Override
    public void run(String... args) throws Exception {
        try {

        Role userRole = new Role("ROLE_USER");
        if(roleRepository.findByName("ROLE_USER")==null) {
            userRole.privileges = Set.of(
                    Privilege.READ_USER,
                    Privilege.UPDATE_USER,
                    Privilege.DELETE_USER,

                    Privilege.CREATE_POST,
                    Privilege.READ_POST,
                    Privilege.UPDATE_POST,
                    Privilege.DELETE_POST,

                    Privilege.CREATE_COMMENT,
                    Privilege.READ_COMMENT,
                    Privilege.DELETE_COMMENT,
                    Privilege.UPDATE_COMMENT
            );
            roleRepository.save(userRole);
        }

        Role adminRole = new Role("ROLE_ADMIN");
        if(roleRepository.findByName("ROLE_USER")==null) {
            adminRole.privileges = Set.of(
                    Privilege.CREATE_USER,
                    Privilege.READ_USER,
                    Privilege.UPDATE_USER,
                    Privilege.DELETE_USER,

                    Privilege.CREATE_POST,
                    Privilege.READ_POST,
                    Privilege.UPDATE_POST,
                    Privilege.DELETE_POST,

                    Privilege.CREATE_COMMENT,
                    Privilege.READ_COMMENT,
                    Privilege.DELETE_COMMENT,
                    Privilege.UPDATE_COMMENT
            );
            roleRepository.save(adminRole);
        }
        }catch (Exception e){
            System.out.println(e.getLocalizedMessage());
        }
    }
}
