package battlearena;

import javafx.scene.paint.Color;

public class Archer extends Fighter {

    public Archer(int playerNumber) {
        super("Archer", 80, 5.0, playerNumber == 1 ? Color.DARKGREEN : Color.FORESTGREEN, playerNumber);
    }

    @Override
    protected void initializeWeapons() {
        addWeapon(new Bow());
        addWeapon(new Pistol());
    }
}
