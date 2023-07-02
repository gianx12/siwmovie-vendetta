package it.uniroma3.siw.repository;

import it.uniroma3.siw.model.User;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User,Long> {

    public boolean existsByEmail(String email);
}
