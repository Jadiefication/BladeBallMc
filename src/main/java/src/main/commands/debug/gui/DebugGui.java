package src.main.commands.debug.gui;

import net.kyori.adventure.text.Component;
import net.minestom.server.inventory.Inventory;
import net.minestom.server.inventory.InventoryType;
import org.jetbrains.annotations.NotNull;
import src.main.core.GUI.Border;

public class DebugGui extends Inventory {

    public DebugGui() {
        super(InventoryType.CHEST_6_ROW, Component.text("Debug Inventory"));
        Border.setInventoryBorder(this);
    }
}
