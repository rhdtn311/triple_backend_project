package kong.review.repository;

import kong.place.domain.Place;
import kong.review.domain.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    public Optional<Review> findFirstByPlaceOrderByRegDateAsc(Place place);

    @Query("select r from Review r join fetch r.user join fetch r.place where r.reviewId = :reviewId")
    public Optional<Review> findReviewByReviewId(@Param(value = "reviewId") String reviewId);
}
