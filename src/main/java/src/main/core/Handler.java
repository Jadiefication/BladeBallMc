package src.main.core;

import net.minestom.server.instance.InstanceContainer;

public interface Handler {

    void start(InstanceContainer container);

    void update(InstanceContainer container);
}
