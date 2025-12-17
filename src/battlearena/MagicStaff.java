package battlearena;

import javafx.scene.paint.Color;

public class MagicStaff extends Weapon {

    public MagicStaff() {
        super("Magic Staff", 20, 400, 600, Color.PURPLE, 8);
    }

    @Override
    public Projectile createProjectile(double startX, double startY, double dirX, double dirY, Fighter owner) {
        return new Projectile(startX, startY, dirX, dirY, this, owner);
    }
}