package it.uniroma3.siw.repository;

import it.uniroma3.siw.model.Artist;
import it.uniroma3.siw.model.Movie;
import jakarta.transaction.Transactional;
import org.springframework.data.repository.CrudRepository;
import java.time.LocalDate;
import java.util.Set;

public interface ArtistRepository extends CrudRepository<Artist,Long> {

    @Transactional
    boolean existsByNameAndSurnameAndBirthDate(String name, String surname, LocalDate birthDate);
    @Transactional
    Set<Artist> getByActedMoviesNotContains(Movie movie);
    /*List <Artist> getByDirectedMoviesNotContaining(Movie movie);*/
}
