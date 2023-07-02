package it.uniroma3.siw.repository;

import it.uniroma3.siw.model.Movie;
import org.springframework.data.repository.CrudRepository;

public interface MovieRepository extends CrudRepository<Movie,Long> {
    boolean existsByTitleAndYear(String title,Integer year);

}
