package io.jadiefication.game.start.team;

public class IncorrectTeamException extends RuntimeException {
    public IncorrectTeamException() {
        super("The team specified is not a proper game team");
    }
}
