package kong.event.dto;

import kong.attached_photo.domain.AttachedPhoto;
import kong.place.domain.Place;
import kong.point.domain.ReviewPoint;
import kong.review.domain.Review;
import kong.user.domain.User;
import kong.event.util.EventType;
import kong.review.util.ReviewAction;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.lang.Nullable;

import java.util.List;

@AllArgsConstructor
@Builder
@Getter
@Setter
public class EventReqDTO {

    private EventType type;

    private ReviewAction action;

    private String reviewId;

    private String content;

    private List<String> attachedPhotoIds;

    private String userId;

    private String placeId;

    public boolean hasContent() {
        return this.content != null;
    }

    public boolean hasAttachedPhoto() {
        return this.attachedPhotoIds.size() != 0;
    }

    public ReviewPoint createReviewPoint(int value, User user, Place place) {
        return ReviewPoint.builder()
                .user(user)
                .place(place)
                .value(value)
                .build();
    }

}
