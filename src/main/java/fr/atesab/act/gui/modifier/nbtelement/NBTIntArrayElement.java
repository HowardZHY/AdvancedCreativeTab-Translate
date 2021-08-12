package fr.atesab.act.gui.modifier.nbtelement;

import fr.atesab.act.gui.modifier.GuiListModifier;
import fr.atesab.act.gui.modifier.nbt.GuiNBTIntArrayModifier;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.IntArrayTag;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;

public class NBTIntArrayElement extends NBTElement {
	private IntArrayTag value;

	public NBTIntArrayElement(GuiListModifier<?> parent, String key, IntArrayTag value) {
		super(parent, key, 200, 21);
		this.value = value;
		buttonList
				.add(new Button(0, 0, 200, 20, new TranslatableComponent("gui.act.modifier.tag.editor.intArray"), b -> {
					mc.setScreen(new GuiNBTIntArrayModifier(new TextComponent(parent.getStringTitle() + key + "/"),
							parent, tag -> NBTIntArrayElement.this.value = tag, value.copy()));
				}));
	}

	@Override
	public NBTElement clone() {
		return new NBTIntArrayElement(parent, key, value.copy());
	}

	@Override
	public Tag get() {
		return value.copy();
	}

	@Override
	public String getType() {
		return I18n.get("gui.act.modifier.tag.editor.intArray") + "[" + value.size() + "]";
	}

}
