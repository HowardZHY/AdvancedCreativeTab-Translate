package fr.atesab.act.gui.modifier.nbtelement;

import fr.atesab.act.gui.modifier.GuiListModifier;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.resources.I18n;
import net.minecraft.nbt.INBT;

public abstract class NBTNumericElement<T extends Number> extends NBTElement {
	private T value;
	private TextFieldWidget field;
	private String type;

	public NBTNumericElement(String type, GuiListModifier<?> parent, String key, int sizeX, int sizeY, T value) {
		super(parent, key, Math.max(sizeX, 200), Math.max(sizeY, 21));
		this.value = value;
		this.type = type;
		fieldList.add(field = new TextFieldWidget(fontRenderer, 2, 2, 196, 16, ""));
		field.setText(String.valueOf(value));
	}

	public NBTNumericElement(String type, GuiListModifier<?> parent, String key, T value) {
		this(type, parent, key, 200, 21, value);
	}

	@Override
	public NBTElement clone() {
		return new NBTNumericElement<T>(type, parent, key, value) {
			@Override
			public INBT get(T value) {
				return NBTNumericElement.this.get(value);
			}

			@Override
			public T parseValue(String text) throws NumberFormatException {
				return NBTNumericElement.this.parseValue(text);
			}

			@Override
			public void setNull() {
				NBTNumericElement.this.setNull();
			}
		};
	}

	@Override
	public INBT get() {
		return get(value);
	}

	public abstract INBT get(T value);

	@Override
	public String getType() {
		return I18n.format("gui.act.modifier.tag.editor." + type);
	}

	public T getValue() {
		return value;
	}

	public void setValue(T value) {
		this.value = value;
	}

	@Override
	public boolean match(String search) {
		return field.getText().toLowerCase().contains(search.toLowerCase()) || super.match(search);
	}

	@Override
	public boolean charTyped(char key, int modifiers) {
		if (super.charTyped(key, modifiers)) {
			try {
				if (field.getText().isEmpty())
					setNull();
				else
					value = parseValue(field.getText());
				field.setTextColor(0xffffffff);
			} catch (NumberFormatException e) {
				field.setTextColor(0xffff0000);
			}
			return true;
		}
		return false;
	}

	@Override
	public boolean keyPressed(int key, int scanCode, int modifiers) {
		if (super.keyPressed(key, scanCode, modifiers)) {
			try {
				if (field.getText().isEmpty())
					setNull();
				else
					value = parseValue(field.getText());
				field.setTextColor(0xffffffff);
			} catch (NumberFormatException e) {
				field.setTextColor(0xffff0000);
			}
			return true;
		}
		return false;
	}

	public abstract void setNull();

	public abstract T parseValue(String text) throws NumberFormatException;

	public void updateValue(T value) {
		this.value = value;
		field.setText("" + value);
		field.setTextColor(0xffffffff);
	}

}