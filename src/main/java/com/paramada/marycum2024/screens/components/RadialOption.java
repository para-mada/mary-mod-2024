package com.paramada.marycum2024.screens.components;

import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class RadialOption {
    public final Text name;
    public final Identifier icon;
    public final Runnable onSelect;

    public RadialOption(Text name, Identifier icon, Runnable onSelect) {
        this.name = name;
        this.icon = icon;
        this.onSelect = onSelect;
    }

    public void onSelect() {
        onSelect.run();
    }
}
