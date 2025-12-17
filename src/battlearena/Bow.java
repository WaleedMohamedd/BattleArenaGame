package battlearena;

import javafx.scene.paint.Color;

public class Bow extends Weapon {

    public Bow() {
        super("Bow", 15, 600, 500, Color.BROWN, 5);
    }

    @Override
    public Projectile createProjectile(double startX, double startY, double dirX, double dirY, Fighter owner) {
        return new Projectile(startX, startY, dirX, dirY, this, owner);
    }
}
