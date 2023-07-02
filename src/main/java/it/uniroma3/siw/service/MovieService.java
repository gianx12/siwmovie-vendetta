package it.uniroma3.siw.service;

import java.io.IOException;
import java.util.Set;

import it.uniroma3.siw.model.Review;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.springframework.web.multipart.MultipartFile;
import it.uniroma3.siw.model.Movie;
import it.uniroma3.siw.model.Artist;
import it.uniroma3.siw.model.Image;
import it.uniroma3.siw.repository.ArtistRepository;
import it.uniroma3.siw.repository.ImageRepository;
import it.uniroma3.siw.repository.MovieRepository;
import jakarta.transaction.Transactional;

@Service
public class MovieService {

    @Autowired
    ArtistRepository artistRepository;

    @Autowired
    MovieRepository movieRepository;

    @Autowired
    ImageRepository imageRepository;

    @Transactional
    public void createMovie(Movie movie, MultipartFile image) throws IOException {
        Image movieImg = new Image(image.getBytes());
        this.imageRepository.save(movieImg);

        movie.setImage(movieImg);
        this.movieRepository.save(movie);
    }

    @Transactional
    public void setActorToMovie(Movie movie, Long artistId) {
        Artist actor = this.artistRepository.findById(artistId).get();

        actor.getActedMovies().add(movie);
        movie.getActors().add(actor);

        this.artistRepository.save(actor);
        this.movieRepository.save(movie);
    }

    @Transactional
    public void removeActorToMovie(Movie movie, Long artistId) {
        Artist actor = this.artistRepository.findById(artistId).get();

        actor.getActedMovies().remove(movie);
        movie.getActors().remove(actor);

        this.artistRepository.save(actor);
        this.movieRepository.save(movie);
    }

    public boolean hasReviewFromAuthor(Long movieId, String username){
        Movie movie = this.movieRepository.findById(movieId).get();
        Set<Review> reviews = movie.getReviews();
        for (Review review: reviews) {
            if(review.getAuthor().equals(username)) {
                return true;
            }
        }
        return false;
    }
}
