package kr.hhplus.be.server.domain.user;

import kr.hhplus.be.server.application.user.UserChargeCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    // todo : exception 수정
    public UserEntity charge(UserChargeCommand userChargeCommand) {
        UserEntity getUser = userRepository.findById(userChargeCommand.getUserId())
                .orElseThrow(()-> new RuntimeException("해당 유저가 존재하지 않습니다."));
        UserEntity rtn = getUser.chargePoint(userChargeCommand.getAmount());
        return userRepository.update(rtn);
    }

}
