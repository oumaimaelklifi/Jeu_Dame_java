public class Player {
    private String name;
    private boolean isBlack;

    public Player(String name, boolean isBlack) {
        this.name = name;
        this.isBlack = isBlack;
    }

    public String getName() {
        return name;
    }

    public boolean isBlack() {
        return isBlack;
    }

    public String getColorName() {
        return isBlack ? GameConstants.BLACK + "Black" + GameConstants.RESET
                : GameConstants.WHITE + "White" + GameConstants.RESET;
    }
}