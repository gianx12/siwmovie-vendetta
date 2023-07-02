package it.uniroma3.siw.controller;

import it.uniroma3.siw.controller.validator.ArtistValidator;
import it.uniroma3.siw.controller.validator.MovieValidator;
import it.uniroma3.siw.model.Artist;
import it.uniroma3.siw.model.Image;
import it.uniroma3.siw.model.Movie;
import it.uniroma3.siw.repository.ArtistRepository;
import it.uniroma3.siw.repository.ImageRepository;
import it.uniroma3.siw.repository.MovieRepository;
import it.uniroma3.siw.service.MovieService;
import it.uniroma3.siw.service.UserService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

@Controller
public class AdminController {

    @Autowired
    private ImageRepository imageRepository;
    @Autowired
    private MovieRepository movieRepository;
    @Autowired
    private ArtistRepository artistRepository;
    @Autowired
    private MovieValidator movieValidator;
    @Autowired
    private ArtistValidator artistValidator;
    @Autowired
    private MovieService movieService;
    @Autowired
    private UserService userService;
    
    @GetMapping("/admin/formNewMovie")
    public String newMovie(Model model){
        model.addAttribute("movie",new Movie());
        return "/admin/formNewMovie.html";
    }

    @PostMapping("/admin/uploadMovie")
    public String newMovie(Model model, @Valid @ModelAttribute("movie") Movie movie, BindingResult bindingResult, @RequestParam("file") MultipartFile image) throws IOException {
        this.movieValidator.validate(movie,bindingResult);
        if(!bindingResult.hasErrors()){
            this.movieService.createMovie(movie, image);
            model.addAttribute("movie", movie);
            model.addAttribute("image", movie.getImage());
            model.addAttribute("userDetails", this.userService.getUserDetails());
            return "movie.html";
        } else {
            return "/admin/formNewMovie.html";
        }
    }

    @GetMapping("/admin/formNewArtist")
    public String formNewArtist(Model model){
        model.addAttribute("artist",new Artist());
        return "/admin/formNewArtist.html";
    }

    @PostMapping("/admin/artist")
    public String newArtist(Model model,@Valid @ModelAttribute("artist") Artist artist, BindingResult bindingResult, @RequestParam("file") MultipartFile image) throws  IOException{
        this.artistValidator.validate(artist, bindingResult);
        if(!bindingResult.hasErrors()){
            Image pic = new Image(image.getBytes());
            this.imageRepository.save(pic);
            artist.setProfilePicture(pic);
            this.artistRepository.save(artist);

            model.addAttribute("artist", artist);
            model.addAttribute("profilePic", pic );
            model.addAttribute("userDetails", this.userService.getUserDetails());
            return "artist.html";
        }
        else {
            return "/admin/formNewArtist.html";
        }
    }

    @GetMapping("/admin/manageMovies")
    public String manageMovies(Model model){
        model.addAttribute("movies", this.movieRepository.findAll());
        return "/admin/manageMovies.html";
    }


    @Transactional
    @GetMapping("/admin/formUpdateMovie/{id}")
    public String formUpdateMovie(@PathVariable("id") Long id, Model model){
        model.addAttribute("movie", this.movieRepository.findById(id).get());
        return "/admin/formUpdateMovie.html";
    }

    @GetMapping("/admin/addDirector/{id}")
    public String addDirector(@PathVariable("id") Long id, Model model){
        model.addAttribute("artists", this.artistRepository.findAll());
        model.addAttribute("movie", this.movieRepository.findById(id).get());
        return "/admin/addDirector.html";
    }

    @Transactional
    @GetMapping("/admin/setDirectorToMovie/{artistId}/{movieId}")
    public String setDirectorToMovie(@PathVariable("artistId") Long artistId, @PathVariable("movieId") Long movieId, Model model){
        Artist director = this.artistRepository.findById(artistId).get();
        Movie movie = this.movieRepository.findById(movieId).get();
        movie.setDirector(director);
        director.getDirectedMovies().add(movie);
        this.artistRepository.save(director);
        this.movieRepository.save(movie);

        model.addAttribute("movie", movie);
        return "/admin/formUpdateMovie.html";
    }

    @Transactional
    @GetMapping("/admin/removeDirectorToMovie/{movieId}")
    public String removeDirectorToMovie(@PathVariable("movieId") Long movieId, Model model){
        Movie movie = this.movieRepository.findById(movieId).get();
        Artist director = movie.getDirector();
        director.getDirectedMovies().remove(movie);
        this.artistRepository.save(director);
        movie.setDirector(null);
        this.movieRepository.save(movie);
        model.addAttribute("movie", movie);
        return "/admin/formUpdateMovie.html";
    }

    @Transactional
    @GetMapping("/admin/updateActorsOnMovie/{id}")
    public String updateActors(@PathVariable("id") Long id,Model model ){

        Set<Artist> actorsToAdd = this.actorsToAdd(id);
        model.addAttribute("movie", this.movieRepository.findById(id).get());
        model.addAttribute("actorsToAdd", actorsToAdd);

        return "/admin/actorsToAdd.html";
    }

    @Transactional
    @GetMapping("/admin/addActorToMovie/{actorId}/{movieId}")
    public String addActorToMovie(@PathVariable("actorId") Long actorId, @PathVariable("movieId") Long movieId, Model model){
        Movie movie = this.movieRepository.findById(movieId).get();
        this.movieService.setActorToMovie(movie, actorId);

        model.addAttribute("movie", movie);
        model.addAttribute("actorsToAdd", actorsToAdd(movieId));

        return "/admin/actorsToAdd.html";
    }

    @Transactional
    @GetMapping("/admin/removeActorFromMovie/{actorId}/{movieId}")
    public String removeActorFromMovie(@PathVariable("actorId") Long actorId, @PathVariable("movieId") Long movieId, Model model){
        Movie movie = this.movieRepository.findById(movieId).get();

        this.movieService.removeActorToMovie(movie, actorId);

        model.addAttribute("movie", movie);
        model.addAttribute("actorsToAdd", actorsToAdd(movieId));
        
        return "/admin/actorsToAdd.html";
    }

    @Transactional
    public Set<Artist> actorsToAdd(Long movieId){
        Set<Artist> actorsToAdd= new HashSet<Artist>();
        actorsToAdd = this.artistRepository.getByActedMoviesNotContains(this.movieRepository.findById(movieId).get());
        return actorsToAdd;
    }
    
}
