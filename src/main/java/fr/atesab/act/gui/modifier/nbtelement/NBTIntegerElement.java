package fr.atesab.act.gui.modifier.nbtelement;

import fr.atesab.act.gui.components.ACTButton;
import fr.atesab.act.gui.modifier.GuiColorModifier;
import fr.atesab.act.gui.modifier.GuiListModifier;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;

public class NBTIntegerElement extends NBTNumericElement<Integer> {

    public NBTIntegerElement(GuiListModifier<?> parent, String key, int value) {
        super("int", parent, key, 200, 42, value);
        buttonList.add(new ACTButton(0, 21, 200, 20, Component.translatable("gui.act.modifier.meta.setColor"), b -> mc.setScreen(new GuiColorModifier(parent, this::updateValue, getValue(), 0xffffff))));
    }

    @Override
    public Tag get(Integer value) {
        return IntTag.valueOf(value);
    }

    @Override
    public void setNull() {
        setValue(0);
    }

    @Override
    public Integer parseValue(String text) {
        return text.isEmpty() ? 0 : Integer.parseInt(text);
    }
}
