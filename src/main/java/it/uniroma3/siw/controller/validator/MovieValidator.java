package it.uniroma3.siw.controller.validator;

import it.uniroma3.siw.model.Movie;
import it.uniroma3.siw.repository.MovieRepository;

import java.time.Year;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class MovieValidator implements Validator {
    @Autowired
    private MovieRepository movieRepository;

    @Override
    public boolean supports(Class<?> clazz) {
        return Movie.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        Movie movie = (Movie) target;
        if(movie.getTitle() != null && movie.getYear() != null
            && movieRepository.existsByTitleAndYear(movie.getTitle(),movie.getYear())){
            errors.reject("movie.duplicate");
        }
        if(movie.getYear() > Year.now().getValue()) errors.reject("movie.year.future");
        if(movie.getYear() < 1900) errors.reject("movie.year.past900");
    }
}
