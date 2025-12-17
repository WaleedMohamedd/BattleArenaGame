package battlearena;

import javafx.scene.paint.Color;

public class Pistol extends Weapon {

    public Pistol() {
        super("Pistol", 12, 500, 400, Color.YELLOW, 6);
    }

    @Override
    public Projectile createProjectile(double startX, double startY, double dirX, double dirY, Fighter owner) {
        return new Projectile(startX, startY, dirX, dirY, this, owner);
    }
}