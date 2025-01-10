package src.main.core.ball.entity;

import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.metadata.other.ArmorStandMeta;
import net.minestom.server.instance.InstanceContainer;
import org.jetbrains.annotations.NotNull;

public class BallEntity extends Entity {
    public BallEntity(Pos pos, InstanceContainer container) {
        super(EntityType.ARMOR_STAND);

        setInstance(container, pos);

        ArmorStandMeta meta = (ArmorStandMeta) getEntityMeta();
        meta.setInvisible(true);
        meta.setSmall(false);     // Ensure it is not a small armor stand
        meta.setHealth(Float.MAX_VALUE);
        meta.setHasNoGravity(true);

        // Increase the hitbox size to cover the entire ball
        setBoundingBox(1.0f, 1.0f, 1.0f);  // Adjust the hitbox size (x, y, z)
    }
}
