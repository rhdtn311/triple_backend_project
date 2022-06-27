package kong.attached_photo.repository;

import kong.attached_photo.domain.AttachedPhoto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AttachedPhotoRepository extends JpaRepository<AttachedPhoto, Long> {
}
