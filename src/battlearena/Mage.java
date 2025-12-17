package battlearena;

import javafx.scene.paint.Color;

public class Mage extends Fighter {

    public Mage(int playerNumber) {
        super("Mage", 90, 4.0, playerNumber == 1 ? Color.DARKVIOLET : Color.MEDIUMPURPLE, playerNumber);
    }

    @Override
    protected void initializeWeapons() {
        addWeapon(new MagicStaff());
        addWeapon(new Cannon());
        addWeapon(new Pistol());
    }
}