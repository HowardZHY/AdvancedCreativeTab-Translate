package fr.atesab.act.command;

import java.util.ArrayList;
import java.util.List;

import fr.atesab.act.command.node.MainCommand;
import fr.atesab.act.command.node.SubCommand;
import fr.atesab.act.gui.modifier.GuiItemStackModifier;
import fr.atesab.act.utils.GuiUtils;
import fr.atesab.act.utils.ItemUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;

public class SCEdit extends SubCommand {

	public SCEdit() {
		super("edit", "", CommandClickOption.doCommand);
	}

	@Override
	public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
		return null;
	}

	@Override
	public List<String> getAlias() {
		return new ArrayList<String>();
	}

	@Override
	public String getDescription() {
		return I18n.format("cmd.act.edit");
	}

	@Override
	public String getSubCommandUsage(ICommandSender sender) {
		return getName();
	}

	@Override
	public void processSubCommand(ICommandSender sender, String[] args, MainCommand mainCommand)
			throws CommandException {
		Minecraft mc = Minecraft.getMinecraft();
		final int slot = mc.thePlayer.inventory.currentItem;
		if (mc.thePlayer.inventory.getCurrentItem() == null)
			Minecraft.getMinecraft().ingameGUI.getChatGUI().printChatMessage(new ChatComponentText("[ACT] Tried to edit null item !"));
		if (mc.thePlayer.inventory.getCurrentItem() != null)
			GuiUtils.displayScreen(new GuiItemStackModifier(null, mc.thePlayer.getHeldItem() != null ? mc.thePlayer.getHeldItem().copy() : null, is -> ItemUtils.give(mc, is, 36 + slot)));
	}

}
