package fr.atesab.act.gui.modifier.nbt;

import fr.atesab.act.gui.modifier.GuiListModifier;
import fr.atesab.act.gui.modifier.GuiStringModifier;
import fr.atesab.act.gui.modifier.nbtelement.NBTElement;
import fr.atesab.act.gui.selector.GuiButtonListSelector;
import fr.atesab.act.utils.Tuple;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.nbt.*;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class GuiNBTModifier extends GuiListModifier<CompoundTag> {

    public static final BiConsumer<Integer, GuiListModifier<?>> ADD_ELEMENT = (i, lm) -> {
        final GuiStringModifier modifier = new GuiStringModifier(lm, Component.translatable("gui.act.modifier.name"),
                "", null);
        modifier.setSetter(key -> {
            if (!key.isEmpty())
                modifier.setParent(addElement(i == null ? lm.getElements().size() - 1 : i, lm, key));
        });
        lm.getMinecraft().setScreen(modifier);
    };

    public static GuiButtonListSelector<Tag> addElement(int i, GuiListModifier<?> lm, String key) {
        return new GuiButtonListSelector<>(lm, Component.translatable("gui.act.modifier.tag.editor"), Arrays.asList(
                new Tuple<>(I18n.get("gui.act.modifier.tag.editor.tag"), new CompoundTag()),
                new Tuple<>(I18n.get("gui.act.modifier.tag.editor.string"), StringTag.valueOf("")),
                new Tuple<>(I18n.get("gui.act.modifier.tag.editor.int"), IntTag.valueOf(0)),
                new Tuple<>(I18n.get("gui.act.modifier.tag.editor.long"), LongTag.valueOf(0L)),
                new Tuple<>(I18n.get("gui.act.modifier.tag.editor.float"), FloatTag.valueOf(0F)),
                new Tuple<>(I18n.get("gui.act.modifier.tag.editor.double"), DoubleTag.valueOf(0D)),
                new Tuple<>(I18n.get("gui.act.modifier.tag.editor.short"), ShortTag.valueOf((short) 0)),
                new Tuple<>(I18n.get("gui.act.modifier.tag.editor.intArray"), new IntArrayTag(new int[0])),
                new Tuple<>(I18n.get("gui.act.modifier.tag.editor.longArray"),
                        new LongArrayTag(new long[0])),
                new Tuple<>(I18n.get("gui.act.modifier.tag.editor.byte"), ByteTag.valueOf((byte) 0)),
                new Tuple<>(I18n.get("gui.act.modifier.tag.editor.list"), new ListTag())), base -> {
            lm.addListElement(i, NBTElement.getElementByBase(lm, key, base));
            return null;
        });
    }

    public static Tag getDefaultElement(int id) {
        return switch (id) {
            case 1 -> ByteTag.valueOf((byte) 0);
            case 2 -> ShortTag.valueOf((short) 0);
            case 3 -> IntTag.valueOf(0);
            case 4 -> LongTag.valueOf(0L);
            case 5 -> FloatTag.valueOf(0F);
            case 6 -> DoubleTag.valueOf(0D);
            case 8 -> StringTag.valueOf("");
            case 9 -> new ListTag();
            case 10 -> new CompoundTag();
            case 11 -> new IntArrayTag(new int[0]);
            case 12 -> new LongArrayTag(new long[0]);
            default -> null;
        };
    }

    public GuiNBTModifier(Screen parent, Consumer<CompoundTag> setter, CompoundTag tag) {
        this(Component.literal("/"), parent, setter, tag);
    }

    @SuppressWarnings("unchecked")
    public GuiNBTModifier(Component title, Screen parent, Consumer<CompoundTag> setter, CompoundTag tag) {
        super(parent, title, new ArrayList<>(), setter, true, true, new Tuple[0]);
        addListElement(new ButtonElementList(200, 21, 200, 20, Component.literal("+").withStyle(ChatFormatting.GREEN),
                () -> ADD_ELEMENT.accept(null, this), null));
        tag.getAllKeys().forEach(key -> addElement(key, tag.get(key)));
        setPaddingLeft(5);
        setPaddingTop(13 + Minecraft.getInstance().font.lineHeight);
        setNoAdaptativeSize(true);
    }

    private void addElement(int i, String key, Tag base) {
        if (getElements().stream().anyMatch(le -> le instanceof NBTElement && ((NBTElement) le).getKey().equals(key))) {
            addElement(key + "_", base);
            return;
        }
        addListElement(i, NBTElement.getElementByBase(this, key, base));

    }

    private void addElement(String key, Tag base) {
        addElement(getElements().size() - 1, key, base);
    }

    @Override
    protected CompoundTag get() {
        CompoundTag tag = new CompoundTag();
        getElements().stream().filter(le -> le instanceof NBTElement).forEach(le -> {
            NBTElement elem = ((NBTElement) le);
            tag.put(elem.getKey(), elem.get());
        });
        return tag;
    }

}
