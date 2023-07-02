package it.uniroma3.siw.model;

import jakarta.persistence.*;

import jakarta.validation.constraints.NotBlank;
import java.util.Set;
@Entity
public class Movie {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotBlank
    private String title;

    private Integer year;

    @OneToOne
    Image image;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Artist director;

    @ManyToMany(mappedBy = "actedMovies", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Set<Artist> actors;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Set<Review> reviews;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public Artist getDirector() {
        return director;
    }

    public void setDirector(Artist director) {
        this.director = director;
    }

    public Set<Artist> getActors() {
        return actors;
    }

    public void setActors(Set<Artist> actors) {
        this.actors = actors;
    }

    public Set<Review> getReviews() {
        return reviews;
    }

    public void setReviews(Set<Review> reviews) {
        this.reviews = reviews;
    }


}
