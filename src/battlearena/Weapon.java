package battlearena;

import javafx.scene.paint.Color;

public abstract class Weapon {
    protected String name;
    protected double damage;
    protected double projectileSpeed;
    protected long cooldownMillis;
    protected Color projectileColor;
    protected double projectileSize;

    public Weapon(String name, double damage, double projectileSpeed, long cooldownMillis, Color projectileColor, double projectileSize) {
        this.name = name;
        this.damage = damage;
        this.projectileSpeed = projectileSpeed;
        this.cooldownMillis = cooldownMillis;
        this.projectileColor = projectileColor;
        this.projectileSize = projectileSize;
    }

    public String getName() {
        return name;
    }
    public double getDamage() {
        return damage;
    }
    public double getProjectileSpeed() {
        return projectileSpeed;
    }
    public long getCooldownMillis() {
        return cooldownMillis;
    }
    public Color getProjectileColor() {
        return projectileColor;
    }
    public double getProjectileSize() {
        return projectileSize;
    }

    public abstract Projectile createProjectile(double startX, double startY, double dirX, double dirY, Fighter owner);

    @Override
    public String toString() {
        return name;
    }
}