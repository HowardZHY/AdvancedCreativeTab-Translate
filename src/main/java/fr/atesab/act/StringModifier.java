package fr.atesab.act;

import java.util.function.Consumer;

import net.minecraft.client.gui.screens.Screen;

public class StringModifier {
	private String string;
	private Consumer<String> setter;
	private Screen nextScreen;

	public String getString() {
		return string;
	}

	public void setString(String string) {
		setter.accept(string);
		this.string = string;
	}

	public Screen getNextScreen() {
		return nextScreen;
	}

	public void setNextScreen(Screen nextScreen) {
		this.nextScreen = nextScreen;
	}

	public StringModifier(String string, Screen nextScreen, Consumer<String> setter) {
		this.string = string;
		this.nextScreen = nextScreen;
		this.setter = setter;
	}

}
