package battlearena;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class Projectile {
    private double x;
    private double y;
    private double directionX;
    private double directionY;
    private double speed;
    private double damage;
    private Fighter owner;
    private Circle visual;
    private boolean active;

    public Projectile(double startX, double startY, double dirX, double dirY, Weapon weapon, Fighter owner) {
        this.x = startX;
        this.y = startY;

        double magnitude = Math.sqrt(dirX * dirX + dirY * dirY);
        if (magnitude > 0) {
            this.directionX = dirX / magnitude;
            this.directionY = dirY / magnitude;
        } else {
            this.directionX = 1;
            this.directionY = 0;
        }

        this.speed = weapon.getProjectileSpeed();
        this.damage = weapon.getDamage();
        this.owner = owner;
        this.active = true;

        this.visual = new Circle(weapon.getProjectileSize());
        this.visual.setFill(weapon.getProjectileColor());
        this.visual.setStroke(Color.BLACK);
        this.visual.setStrokeWidth(1);
        updateVisualPosition();
    }

    public void update(double deltaTime) {
        if (!active) return;

        x += directionX * speed * deltaTime;
        y += directionY * speed * deltaTime;
        updateVisualPosition();
    }

    private void updateVisualPosition() {
        visual.setCenterX(x);
        visual.setCenterY(y);
    }

    public boolean checkCollision(Fighter target) {
        if (!active || target == owner || target.isDead()) return false;

        double targetX = target.getX();
        double targetY = target.getY();
        double targetWidth = target.getWidth();
        double targetHeight = target.getHeight();

        return x >= targetX && x <= targetX + targetWidth && y >= targetY && y <= targetY + targetHeight;
    }

    public boolean isOutOfBounds(double arenaWidth, double arenaHeight) {
        return x < -50 || x > arenaWidth + 50 || y < -50 || y > arenaHeight + 50;
    }

    public double getX() {
        return x;
    }
    public double getY() {
        return y;
    }
    public double getDamage() {
        return damage;
    }
    public Fighter getOwner() {
        return owner;
    }
    public Circle getVisual() {
        return visual;
    }
    public boolean isActive() {
        return active;
    }
    public void setActive(boolean active) {
        this.active = active;
    }
}