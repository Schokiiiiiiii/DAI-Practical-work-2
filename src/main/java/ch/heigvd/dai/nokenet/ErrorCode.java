package ch.heigvd.dai.nokenet;

public enum ErrorCode {
    NOT_COMMAND(10),
    USERNAME_TAKEN(210),
    EXISTING_LOBBY(310),
    NO_LOBBY(320),
    LOBBY_FULL(321),
    NOT_NOW(410);

    private final int code;

    ErrorCode(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
