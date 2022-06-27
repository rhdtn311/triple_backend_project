package kong.point.service;

import kong.user.domain.User;
import kong.user.exception.UserNotFoundException;
import kong.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class PointCheckService {

    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public int getTotalPoints(String userId) {

        User user = userRepository.findUserByUserId(userId).orElseThrow(UserNotFoundException::new);

        return user.getTotalPoints();
    }
}
