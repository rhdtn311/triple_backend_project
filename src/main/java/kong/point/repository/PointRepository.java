package kong.point.repository;

import kong.place.domain.Place;
import kong.point.domain.Point;
import kong.point.domain.ReviewPoint;
import kong.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PointRepository<T extends Point> extends JpaRepository<T, Long> {

    @Query("select p from ReviewPoint p where p.user = :user and p.place = :place")
    public List<ReviewPoint> findReviewPointsByUserAndPlace(@Param(value = "user") User user, @Param(value = "place") Place place);
}
