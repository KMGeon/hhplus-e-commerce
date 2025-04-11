package kr.hhplus.be.server.interfaces.user;

public record UserResponseDTO(){
    public record UserPointResponse(
            long amount
    ) {
    }
}
