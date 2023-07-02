package it.uniroma3.siw.controller.validator;

import it.uniroma3.siw.model.Artist;
import it.uniroma3.siw.repository.ArtistRepository;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class ArtistValidator implements Validator {

    @Autowired
    private ArtistRepository artistRepository;

    @Override
    public boolean supports(Class<?> clazz) {
        return Artist.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        Artist artist = (Artist) target;
        if(artist.getName() != null && artist.getSurname() != null
            && this.artistRepository.existsByNameAndSurnameAndBirthDate(artist.getName(), artist.getSurname(), artist.getBirthDate())){
            errors.reject("artist.duplicate");
        }
        if(artist.getBirthDate().isAfter(LocalDate.now())) errors.reject("artist.birthDate.future");
        if(artist.getBirthDate().isBefore(LocalDate.of(1850, 1, 1))) errors.reject("artist.birthDate.past1850");
        if(artist.getDeathDate()!= null){
            if(artist.getDeathDate().isBefore(artist.getBirthDate())) errors.reject("artist.deathDate.pastBirth");
            if(artist.getDeathDate().isAfter(LocalDate.now())) errors.reject("artist.deathDate.future");
        }
    }
}
