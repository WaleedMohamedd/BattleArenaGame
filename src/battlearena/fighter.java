
package battlearena;

import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.transform.Rotate;
import java.util.ArrayList;
import java.util.List;

public abstract class Fighter {
    private String name;
    private double health;
    private double maxHealth;
    private double x;
    private double y;
    private double speed;
    private double width;
    private double height;
    private Weapon currentWeapon;
    private List<Weapon> availableWeapons;
    private int currentWeaponIndex;
    private long lastShotTime;
    private Polygon visual;
    private Rotate rotation;
    private double facingAngle;
    private int playerNumber;
    private Color color;

    public Fighter(String name, double maxHealth, double speed, Color color, int playerNumber) {
        this.name = name;
        this.maxHealth = maxHealth;
        this.health = maxHealth;
        this.speed = speed;
        this.color = color;
        this.playerNumber = playerNumber;
        this.width = 40;
        this.height = 40;
        this.lastShotTime = 0;
        this.facingAngle = playerNumber == 1 ? 0 : 180;
        this.availableWeapons = new ArrayList<>();
        this.currentWeaponIndex = 0;

        initializeWeapons();
        createVisual();
    }

    protected abstract void initializeWeapons();

    private void createVisual() {
        visual = new Polygon();
        visual.getPoints().addAll(0.0, 0.0, width, height / 2, 0.0, height);
        visual.setFill(color);
        visual.setStroke(Color.BLACK);
        visual.setStrokeWidth(2);

        rotation = new Rotate(facingAngle, width / 2, height / 2);
        visual.getTransforms().add(rotation);
    }

    public void move(double dx, double dy, double minX, double maxX, double minY, double maxY)
    {
        double newX = x + dx * speed;
        double newY = y + dy * speed;

        newX = Math.max(minX, Math.min(maxX - width, newX));
        newY = Math.max(minY, Math.min(maxY - height, newY));

        this.x = newX;
        this.y = newY;
        updateVisualPosition();

        if (dx != 0 || dy != 0)
        {
            facingAngle = Math.toDegrees(Math.atan2(dy, dx));
            rotation.setAngle(facingAngle);
        }
    }

    public void rotateTo(double angle)
    {
        this.facingAngle = angle;
        rotation.setAngle(facingAngle);
    }

    public void rotateTowards(double targetX, double targetY)
    {
        double centerX = x + width / 2;
        double centerY = y + height / 2;
        double angle = Math.toDegrees(Math.atan2(targetY - centerY, targetX - centerX));
        rotateTo(angle);
    }

    private void updateVisualPosition()
    {
        visual.setLayoutX(x);
        visual.setLayoutY(y);
    }

    public void setPosition(double x, double y)
    {
        this.x = x;
        this.y = y;
        updateVisualPosition();
    }

    public void takeDamage(double damage)
    {
        this.health = Math.max(0, this.health - damage);
    }

    public boolean canShoot()
    {
        long currentTime = System.currentTimeMillis();
        return (currentTime - lastShotTime) >= currentWeapon.getCooldownMillis();
    }

    public Projectile shoot()
    {
        if (!canShoot() || isDead()) return null;

        lastShotTime = System.currentTimeMillis();

        double startX = x + width / 2;
        double startY = y + height / 2;

        double dirX = Math.cos(Math.toRadians(facingAngle));
        double dirY = Math.sin(Math.toRadians(facingAngle));

        return currentWeapon.createProjectile(startX, startY, dirX, dirY, this);
    }

    public void switchWeaponNext()
    {
        if (availableWeapons.size() <= 1) return;
        currentWeaponIndex = (currentWeaponIndex + 1) % availableWeapons.size();
        currentWeapon = availableWeapons.get(currentWeaponIndex);
    }

    public void switchWeaponPrevious()
    {
        if (availableWeapons.size() <= 1) return;
        currentWeaponIndex = (currentWeaponIndex - 1 + availableWeapons.size()) % availableWeapons.size();
        currentWeapon = availableWeapons.get(currentWeaponIndex);
    }

    public boolean isDead()
    {
        return health <= 0;
    }

    public double getHealthPercentage()
    {
        return health / maxHealth;
    }

    protected void addWeapon(Weapon weapon)
    {
        availableWeapons.add(weapon);
        if (currentWeapon == null) {
            currentWeapon = weapon;
        }
    }

    public String getName()
    {
        return name;
    }
    public double getHealth()
    {
        return health;
    }
    public double getMaxHealth()
    {
        return maxHealth;
    }
    public double getX()
    {
        return x;
    }
    public double getY()
    {
        return y;
    }
    public double getSpeed()
    {
        return speed;
    }
    public double getWidth()
    {
        return width;
    }
    public double getHeight()
    {
        return height;
    }
    public Weapon getCurrentWeapon()
    {
        return currentWeapon;
    }
    public Node getVisual()
    {
        return visual;
    }
    public int getPlayerNumber()
    {
        return playerNumber;
    }
    public double getFacingAngle()
    {
        return facingAngle;
    }
    public Color getColor()
    {
        return color;
    }
    public List<Weapon> getAvailableWeapons()
    {
        return new ArrayList<>(availableWeapons);
    }

    public void setCurrentWeapon(Weapon weapon)
    {
        if (availableWeapons.contains(weapon))
        {
            this.currentWeapon = weapon;
            this.currentWeaponIndex = availableWeapons.indexOf(weapon);
        }
    }

    public void setHealth(double health)
    {
        this.health = Math.max(0, Math.min(maxHealth, health));
    }

    public void reset()
    {
        this.health = maxHealth;
        this.lastShotTime = 0;
        this.currentWeaponIndex = 0;
        if (!availableWeapons.isEmpty()) {
            this.currentWeapon = availableWeapons.get(0);
        }
        this.facingAngle = playerNumber == 1 ? 0 : 180;
        rotation.setAngle(facingAngle);
    }
}
