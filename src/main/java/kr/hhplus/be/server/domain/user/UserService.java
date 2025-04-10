package kr.hhplus.be.server.domain.user;

import kr.hhplus.be.server.domain.order.OrderCommand;
import kr.hhplus.be.server.domain.user.command.UserChargeCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;


    public void isValidUser(long userId) {
        userRepository.findById(userId)
                .orElseThrow(()-> new RuntimeException("해당 유저가 존재하지 않습니다."));
    }

    public UserEntity charge(UserChargeCommand userChargeCommand) {
        UserEntity getUser = userRepository.findById(userChargeCommand.getUserId())
                .orElseThrow(()-> new RuntimeException("해당 유저가 존재하지 않습니다."))
                .chargePoint(userChargeCommand.getAmount());
        return userRepository.update(getUser);
    }
}
