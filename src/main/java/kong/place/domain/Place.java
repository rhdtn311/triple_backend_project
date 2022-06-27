package kong.place.domain;

import kong.point.domain.ReviewPoint;
import kong.review.domain.Review;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "place")
@Entity
public class Place {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "place_id")
    private String placeId;

    @OneToMany(mappedBy = "place")
    private List<Review> reviews = new ArrayList<>();

    @OneToMany(mappedBy = "place")
    private List<ReviewPoint> reviewPoints = new ArrayList<>();
}
