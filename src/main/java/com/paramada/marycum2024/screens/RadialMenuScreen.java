package com.paramada.marycum2024.screens;

import com.paramada.marycum2024.screens.components.RadialOption;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

import java.util.List;

public class RadialMenuScreen extends Screen {
    private final List<RadialOption> options;
    private int centerX, centerY;
    private float radius = 60f;

    public RadialMenuScreen(List<RadialOption> options) {
        super(Text.literal("Radial Menu"));
        this.options = options;
    }

    @Override
    protected void init() {
        centerX = width / 2;
        centerY = height / 2;
    }

    @Override
    public void render(DrawContext matrices, int mouseX, int mouseY, float delta) {
        renderBackground(matrices, mouseX, mouseY, delta);
        RadialMenuRenderer.render(matrices, centerX, centerY, radius, options, mouseX, mouseY);
        super.render(matrices, mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
            RadialOption selected = RadialMenuRenderer.getHoveredOption(centerX, centerY, options, mouseX, mouseY);
            if (selected != null) selected.onSelect();
            client.setScreen(null); // Cierra el menú
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
