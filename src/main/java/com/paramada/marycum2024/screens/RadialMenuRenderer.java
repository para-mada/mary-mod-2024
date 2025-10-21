package com.paramada.marycum2024.screens;

import com.mojang.blaze3d.systems.RenderSystem;
import com.paramada.marycum2024.screens.components.RadialOption;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

import java.util.List;

import static org.lwjgl.opengl.GL11.*;

public class RadialMenuRenderer {

    public static void render(DrawContext matrices, int cx, int cy, float radius, List<RadialOption> options, double mouseX, double mouseY) {
        int count = options.size();
        double angleStep = (2 * Math.PI) / count;
        double mouseAngle = Math.atan2(mouseY - cy, mouseX - cx);

        for (int i = 0; i < count; i++) {
            double startAngle = i * angleStep;
            double endAngle = startAngle + angleStep;

            boolean hovered = isAngleBetween(mouseAngle, startAngle, endAngle);

            int color = hovered ? 0x80FFFFFF : 0x40FFFFFF;
            fillCircleSector(matrices, cx, cy, radius, startAngle, endAngle, color);

            RadialOption opt = options.get(i);
            drawIcon(matrices, opt.icon, cx + (int)(Math.cos((startAngle + endAngle)/2) * radius * 0.6),
                    cy + (int)(Math.sin((startAngle + endAngle)/2) * radius * 0.6));
        }
    }

    private static boolean isAngleBetween(double angle, double start, double end) {
        angle = (angle + 2*Math.PI) % (2*Math.PI);
        start = (start + 2*Math.PI) % (2*Math.PI);
        end = (end + 2*Math.PI) % (2*Math.PI);
        if (start < end) return angle >= start && angle <= end;
        else return angle >= start || angle <= end;
    }

    private static void fillCircleSector(DrawContext matrices, int cx, int cy, float radius, double startAngle, double endAngle, int color) {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        glBegin(GL_TRIANGLE_FAN);
        glColor4f(((color>>16)&255)/255f, ((color>>8)&255)/255f, (color&255)/255f, ((color>>24)&255)/255f);
        glVertex2f(cx, cy);
        for (double a = startAngle; a <= endAngle; a += Math.PI / 30)
            glVertex2f((float)(cx + Math.cos(a) * radius), (float)(cy + Math.sin(a) * radius));
        glEnd();
        RenderSystem.disableBlend();
    }

    private static void drawIcon(DrawContext matrices, Identifier icon, int x, int y) {
        matrices.drawTexture(icon, x - 8, y - 8, 0, 0, 16, 16, 16, 16);
    }

    public static RadialOption getHoveredOption(int cx, int cy, List<RadialOption> options, double mouseX, double mouseY) {
        double angle = Math.atan2(mouseY - cy, mouseX - cx);
        int index = (int)(((angle + Math.PI) / (2 * Math.PI)) * options.size()) % options.size();
        return options.get(index);
    }
}
