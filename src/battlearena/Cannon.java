package battlearena;

import javafx.scene.paint.Color;

public class Cannon extends Weapon {

    public Cannon() {
        super("Cannon", 30, 300, 1000, Color.ORANGE, 12);
    }

    @Override
    public Projectile createProjectile(double startX, double startY, double dirX, double dirY, Fighter owner) {
        return new Projectile(startX, startY, dirX, dirY, this, owner);
    }
}
