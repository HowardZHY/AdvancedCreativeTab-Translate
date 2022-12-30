package fr.atesab.act.gui;

import com.mojang.blaze3d.vertex.PoseStack;
import fr.atesab.act.ACTMod;
import fr.atesab.act.gui.modifier.GuiColorModifier;
import fr.atesab.act.utils.GuiUtils;
import fr.atesab.act.utils.Tuple;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.language.I18n;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ColorList {
    private final Screen parent;
    private final Minecraft mc;
    private final Font fontRenderer;
    private final List<Integer> list;
    private final String title;
    private final int maxElement;
    private final int sizeX;
    public int x;
    public int y;

    public ColorList(Screen parent, int x, int y, int sizeX, int[] list, String title, int maxElement) {
        this.list = new ArrayList<>(list.length);
        Arrays.stream(list).forEach(this.list::add);
        this.title = title;
        if (maxElement < 1)
            throw new IllegalArgumentException("maxElement must be positive");
        this.maxElement = maxElement;
        this.sizeX = sizeX;
        this.x = x;
        this.y = y;
        this.mc = Minecraft.getInstance();
        this.fontRenderer = this.mc.font;
        this.parent = parent;
    }

    public int[] getColors() {
        return list.stream().mapToInt(i -> i).toArray();
    }

    public void drawNext(PoseStack matrixStack, int mouseX, int mouseY, float zLevel) {
        drawNext(matrixStack, mouseX, mouseY, zLevel, 0, 0);
    }

    public void drawNext(PoseStack matrixStack, int mouseX, int mouseY, float zLevel, int offsetX, int offsetY) {
        offsetX += this.x;
        offsetY += this.y + fontRenderer.lineHeight + 1;
        int i;
        for (i = 0; i < list.size(); i++) {
            int x = offsetX + (16) * (i % sizeX);
            int y = offsetY + (16) * (i / sizeX);
            if (GuiUtils.isHover(x, y, 15, 15, mouseX, mouseY)) {
                List<String> text = Arrays.asList(
                        ChatFormatting.GOLD + "[" + ChatFormatting.YELLOW + I18n.get("gui.act.leftClick")
                                + ChatFormatting.GOLD + "] " + ChatFormatting.YELLOW + I18n.get("gui.act.edit"),
                        ChatFormatting.GOLD + "[" + ChatFormatting.YELLOW + I18n.get("gui.act.rightClick")
                                + ChatFormatting.GOLD + "] " + ChatFormatting.YELLOW + I18n.get("gui.act.delete"));
                int width = text.stream().mapToInt(fontRenderer::width).max().getAsInt(),
                        height = text.size() * (fontRenderer.lineHeight + 1);
                Tuple<Integer, Integer> pos = GuiUtils.getRelativeBoxPos(mouseX, mouseY, width, height, parent.width,
                        parent.height);
                GuiUtils.drawBox(matrixStack, pos.a, pos.b, width, height, zLevel);
                pos.b += 1;
                text.forEach(s -> {
                    ACTMod.drawString(fontRenderer, s, pos.a, pos.b, 0xffffffff);
                    pos.b += 1 + fontRenderer.lineHeight;
                });
            }
        }
    }

    public void draw(PoseStack matrixStack, int mouseX, int mouseY, float zLevel) {
        draw(matrixStack, mouseX, mouseY, zLevel, 0, 0);
    }

    public void draw(PoseStack matrixStack, int mouseX, int mouseY, float zLevel, int offsetX, int offsetY) {
        offsetX += this.x;
        offsetY += this.y;
        GuiUtils.drawCenterString(fontRenderer, title, offsetX + (sizeX * (16)) / 2, offsetY, 0xffffffff);
        offsetY += fontRenderer.lineHeight + 1;
        int i;
        for (i = 0; i < list.size(); i++) {
            int x = offsetX + (16) * (i % sizeX);
            int y = offsetY + (16) * (i / sizeX);
            int c = list.get(i) + 0xff000000;
            GuiUtils.drawGradientRect(matrixStack, x, y, x + 15, y + 15, c, c, zLevel);
        }
        if (i < maxElement) {
            int x = offsetX + (16) * (i % sizeX);
            int y = offsetY + (16) * (i / sizeX);
            boolean flag = GuiUtils.isHover(x, y, 15, 15, mouseX, mouseY);
            int c = flag ? 0xffbbbbbb : 0xff999999;
            GuiUtils.drawGradientRect(matrixStack, x, y, x + 15, y + 15, c, c, zLevel);
            GuiUtils.drawCenterString(fontRenderer, "+", x + 8, y, flag ? 0xff00ff00 : 0xff00aa00, 16);
        }
    }

    public void mouseClick(int mouseX, int mouseY, int mouseButton) {
        int i;
        for (i = 0; i < list.size(); i++) {
            int x = this.x + (16) * (i % sizeX);
            int y = this.y + fontRenderer.lineHeight + 1 + (16) * (i / sizeX);
            if (GuiUtils.isHover(x, y, 15, 15, mouseX, mouseY)) {
                if (mouseButton == 0) {
                    int c = list.get(i);
                    int fi = i;
                    mc.setScreen(new GuiColorModifier(parent, nc -> list.set(fi, nc), c, 0xffffff));
                } else if (mouseButton == 1)
                    list.remove(i);
                else
                    return;
                GuiACT.playClick();
                return;
            }
        }
        if (i < maxElement && mouseButton == 0) {
            int ex = this.x + (16) * (i % sizeX);
            int ey = this.y + fontRenderer.lineHeight + 1 + (16) * (i / sizeX);
            if (GuiUtils.isHover(ex, ey, 15, 15, mouseX, mouseY)) {
                GuiACT.playClick();
                list.add(0xff000000 | ACTMod.RANDOM.nextInt(0x1000000));
            }
        }
    }
}
