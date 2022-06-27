package kong.point.domain;

import kong.place.domain.Place;
import kong.review.domain.Review;
import kong.user.domain.User;
import lombok.*;

import javax.persistence.*;

@NoArgsConstructor
@Getter
@Setter
@Entity
@DiscriminatorValue("REVIEW")
public class ReviewPoint extends Point {

//    @ManyToOne
//    private Review review;

    @ManyToOne
    private Place place;

    @Builder
    public ReviewPoint(int value, User user, Place place) {
        super(value, user);
        this.place = place;
//        this.review = review;
    }
}
