package it.uniroma3.siw.controller;

import it.uniroma3.siw.controller.validator.CredentialsValidator;
import it.uniroma3.siw.controller.validator.ReviewValidator;
import it.uniroma3.siw.controller.validator.UserValidator;
import it.uniroma3.siw.model.Artist;
import it.uniroma3.siw.model.Credentials;
import it.uniroma3.siw.model.Image;
import it.uniroma3.siw.model.Movie;
import it.uniroma3.siw.model.Review;
import it.uniroma3.siw.model.User;
import it.uniroma3.siw.repository.ArtistRepository;
import it.uniroma3.siw.repository.MovieRepository;
import it.uniroma3.siw.repository.ReviewRepository;
import it.uniroma3.siw.service.CredentialsService;
import it.uniroma3.siw.service.MovieService;
import it.uniroma3.siw.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;


@Controller
public class GlobalController {
    @Autowired
    private CredentialsService credentialsService;

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private ArtistRepository artistRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private UserValidator userValidator;

    @Autowired
    private CredentialsValidator credentialsValidator;

    @Autowired
    private MovieService movieService;

    @Autowired
    private ReviewValidator reviewValidator;


    @GetMapping("/")
    public String index(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = null;
        Credentials credentials = null;
        if(!(authentication instanceof AnonymousAuthenticationToken)){
            userDetails = (UserDetails)authentication.getPrincipal();
            credentials = this.credentialsService.getCredentials(userDetails.getUsername());
        }
        if(credentials != null && credentials.getRole().equals(Credentials.ADMIN_ROLE)) return "admin/indexAdmin.html";

        /*model.addAttribute("userDetails", userDetails);*/
        model.addAttribute("movies", this.movieRepository.findAll());
        return "index.html";
    }
    @GetMapping("/index")
    public String index2(Model model){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = null;
        Credentials credentials = null;
        if(!(authentication instanceof AnonymousAuthenticationToken)){
            userDetails = (UserDetails)authentication.getPrincipal();
            credentials = credentialsService.getCredentials(userDetails.getUsername());
        }
        if(credentials != null && credentials.getRole().equals(Credentials.ADMIN_ROLE)) return "admin/indexAdmin.html";

        model.addAttribute("userDetails", userDetails);
        model.addAttribute("movies", this.movieRepository.findAll());
        return "index.html";
    }

    @GetMapping(value = "/login")
    public String showLoginForm (Model model) {
        return "formLogin.html";
    }

    @GetMapping(value = "/register")
    public String showRegisterForm (Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("credentials", new Credentials());
        return "formRegister.html";
    }

    /* ridondante, ma viene usata anche per la admin dashboard */
    @GetMapping("/movies")
    public String movies(Model model){

        model.addAttribute("movies", this.movieRepository.findAll());
        return "index.html";
    }

    @GetMapping("/artists")
    public String artists(Model model){

        model.addAttribute("artists", this.artistRepository.findAll());
        return "artists.html";
    }

    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("user") User user,
                               BindingResult userBindingResult, @Valid
                               @ModelAttribute("credentials") Credentials credentials,
                               BindingResult credentialsBindingResult,
                               Model model) {
        this.userValidator.validate(user,userBindingResult);
        this.credentialsValidator.validate(credentials, credentialsBindingResult);                        
        if(!userBindingResult.hasErrors() && ! credentialsBindingResult.hasErrors()) {
            credentials.setUser(user);
            credentialsService.saveCredentials(credentials);
            userService.saveUser(user);
            model.addAttribute("user", user);
            return "formLogin.html";
        }
        return "formRegister.html";
    }

    @GetMapping("/artist/{id}")
    public String artist(@PathVariable("id") Long id, Model model){

        model.addAttribute("userDetails", this.userService.getUserDetails());

        Artist artist = this.artistRepository.findById(id).get();
        Image profilePic = artist.getProfilePicture(); //è una string rappresentante l'immagine in base64
        model.addAttribute("artist", this.artistRepository.findById(id).get());
        model.addAttribute("profilePic", profilePic);
        
        return "artist.html";
    }

    @GetMapping("/movie/{id}")
    public String movie(@PathVariable("id") Long id, Model model) {

        UserDetails userDetails = this.userService.getUserDetails();
        model.addAttribute("userDetails", userDetails);

        Movie movie = this.movieRepository.findById(id).get();
        Image image = movie.getImage();
        
        model.addAttribute("movie", movie);
        model.addAttribute("image", image);
        
        /* Gestione della review */
        if (userDetails != null){
            if(this.credentialsService.getCredentials(userDetails.getUsername()) !=null){
                model.addAttribute("review", new Review());
            }
        }

        if(userDetails != null && this.credentialsService.getCredentials(userDetails.getUsername()).getRole().equals(Credentials.ADMIN_ROLE)){
            model.addAttribute("admin", true);
        }
        return "movie.html";
    }
    
    @PostMapping("/user/review/{movieId}")
    public String addReview(Model model, @Valid @ModelAttribute("review") Review review, BindingResult bindingResult, @PathVariable("movieId") Long id){
        this.reviewValidator.validate(review,bindingResult);
        Movie movie = this.movieRepository.findById(id).get();
        String username = this.userService.getUserDetails().getUsername();

        if(!bindingResult.hasErrors() && !this.movieService.hasReviewFromAuthor(id, username)){
            if(this.userService.getUserDetails() != null && !movie.getReviews().contains(review)){
                review.setAuthor(username);
                this.reviewRepository.save(review);
                movie.getReviews().add(review);
            }
        }
        this.movieRepository.save(movie);

        if(this.userService.getUserDetails() != null && !movie.getReviews().contains(review)){
            if(!this.movieService.hasReviewFromAuthor(id, username)){
                this.reviewRepository.save(review);
                movie.getReviews().add(review);
            }
            else{
                model.addAttribute("reviewError", "Already Reviewed!");
            }

        }
        this.movieRepository.save(movie);
        
        model.addAttribute("movie", movie);
        model.addAttribute("image", movie.getImage());

        if(this.credentialsService.getCredentials(username).getRole().equals(Credentials.ADMIN_ROLE)){
            model.addAttribute("admin", true);
        }
        return "movie.html";
    }

    @GetMapping("/admin/deleteReview/{movieId}/{reviewId}")
    public String removeReview(Model model, @PathVariable("movieId") Long movieId,@PathVariable("reviewId") Long reviewId){
        Movie movie = this.movieRepository.findById(movieId).get();
        Review review = this.reviewRepository.findById(reviewId).get();
        UserDetails userDetails = this.userService.getUserDetails();

        movie.getReviews().remove(review);
        this.reviewRepository.delete(review);
        this.movieRepository.save(movie);

        model.addAttribute("movie", movie);
        model.addAttribute("image", movie.getImage());

        if (userDetails != null){
            if(this.credentialsService.getCredentials(userDetails.getUsername()) !=null ){
                model.addAttribute("review", new Review());
            }
            if(this.movieService.hasReviewFromAuthor(movieId, userDetails.getUsername())){
                model.addAttribute("reviewError", "You have already reviewed this movie.");
            }
            
        }
        return "movie.html";
    }

}
