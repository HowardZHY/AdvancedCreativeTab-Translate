package fr.atesab.act.gui.modifier;

import java.util.function.Consumer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.init.SoundEvents;

public class GuiModifier<T> extends GuiScreen {
	public static void playClick() {
		Minecraft.getMinecraft().getSoundHandler()
				.playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0F));
	}

	protected GuiScreen parent;

	protected Consumer<T> setter;

	public GuiModifier(GuiScreen parent, Consumer<T> setter) {
		this.parent = parent;
		this.setter = setter;
	}

	@Override
	public boolean doesGuiPauseGame() {
		return false;
	}

	public GuiScreen getParent() {
		return parent;
	}

	public void set(T value) {
		setter.accept(value);
	}

	public void setParent(GuiScreen parent) {
		this.parent = parent;
	}
}