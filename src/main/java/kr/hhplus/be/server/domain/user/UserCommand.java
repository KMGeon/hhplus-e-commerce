package kr.hhplus.be.server.domain.user;

public class UserCommand {
    public  record PointCharge(Long userId, Long amount) {
    }
}
