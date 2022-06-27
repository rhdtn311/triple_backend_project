package kong.review.domain;

import kong.BaseEntity;
import kong.attached_photo.domain.AttachedPhoto;
import kong.point.domain.ReviewPoint;
import kong.user.domain.User;
import kong.place.domain.Place;
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
@Table(name = "review")
@Entity
public class Review extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "review_id")
    private String reviewId;

    @Column(name = "content")
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "place_id")
    private Place place;

    @OneToMany(mappedBy = "review")
    private List<AttachedPhoto> attachedPhotos = new ArrayList<>();

//    @OneToMany(mappedBy = "review")
//    private List<ReviewPoint> reviewPoints = new ArrayList<>();

    public void setContent(String content) {
        this.content = content;
    }
}
