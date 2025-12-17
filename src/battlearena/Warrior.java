package battlearena;

import javafx.scene.paint.Color;

public class Warrior extends Fighter {

    public Warrior(int playerNumber) {
        super("Warrior", 120, 3.5, playerNumber == 1 ? Color.DARKRED : Color.CRIMSON, playerNumber);
    }

    @Override
    protected void initializeWeapons() {
        addWeapon(new Cannon());
        addWeapon(new Pistol());
    }
}