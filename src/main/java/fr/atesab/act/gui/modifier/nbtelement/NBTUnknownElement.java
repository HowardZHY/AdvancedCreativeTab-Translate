package fr.atesab.act.gui.modifier.nbtelement;

import fr.atesab.act.gui.modifier.GuiListModifier;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.nbt.Tag;

public class NBTUnknownElement extends NBTElement {
    private final Tag value;

    public NBTUnknownElement(GuiListModifier<?> parent, String key, Tag value) {
        super(parent, key, 200, 21);
        this.value = value;
    }

    @Override
    public NBTElement clone() {
        return new NBTUnknownElement(parent, key, value.copy());
    }

    @Override
    public Tag get() {
        return value.copy();
    }

    @Override
    public String getType() {
        return I18n.get("gui.act.modifier.tag.editor.unknown");
    }

}
