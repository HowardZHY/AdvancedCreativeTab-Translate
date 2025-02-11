package fr.atesab.act.command.node;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import fr.atesab.act.utils.ChatUtils;
import fr.atesab.act.utils.CommandUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.HoverEvent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;

public abstract class MainCommand implements ICommand {
	public List<SubCommand> subCommands = new ArrayList<SubCommand>();
	public String defaultCommand;

	public MainCommand(List<SubCommand> subCommands2, String defaultCommand) {
		this.subCommands = subCommands2;
		this.defaultCommand = defaultCommand;
	}

	public boolean canCommandSenderUseCommand(ICommandSender sender) {
		return true;
	}

	@Override
	public int compareTo(ICommand p_compareTo_1_) {
		return this.getCommandName().compareTo(p_compareTo_1_.getCommandName());
	}

	@Override
	public void processCommand(ICommandSender sender, String[] args) throws CommandException {
		Minecraft mc = Minecraft.getMinecraft();
		if (args.length == 0)
			args = new String[] { defaultCommand };
		for (int i = 0; i < subCommands.size(); i++) {
			List<String> alias = subCommands.get(i).getAlias();
			if (!(alias = alias == null ? new ArrayList<String>() : alias).contains(subCommands.get(i).getName()))
				alias.add(subCommands.get(i).getName());
			for (int j = 0; j < alias.size(); j++) {
				if (args[0].equals(alias.get(j))) {
					String[] SCargs = new String[args.length - 1];
					System.arraycopy(args, 1, SCargs, 0, SCargs.length);
					subCommands.get(i).processSubCommand(sender, SCargs, this);
					return;
				}
			}
		}
		ChatUtils.send(new ChatComponentText(
				I18n.format("cmd.act.mc.invalid", "/" + this.getCommandName() + " " + defaultCommand).replaceAll("::", " "))
						.setChatStyle(new ChatStyle()
								.setChatClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
										"/" + this.getCommandName() + " " + defaultCommand))
								.setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
										new ChatComponentText(I18n.format("cmd.act.help.do"))
												.setChatStyle(new ChatStyle().setColor(EnumChatFormatting.BLUE))))
								.setColor(EnumChatFormatting.RED)));
	}

	@Override
	public abstract List<String> getCommandAliases();

	@Override
	public abstract String getCommandName();

	@Override
	public List<String> addTabCompletionOptions(ICommandSender sender, String[] args,
			BlockPos targetPos) {
		ArrayList<String> ls = new ArrayList<String>();
		subCommands.sort(new Comparator<SubCommand>() {
			public int compare(SubCommand o1, SubCommand o2) {
				return o1.getName().toLowerCase().compareTo(o2.getName().toLowerCase());
			}
		});
		for (int i = 0; i < subCommands.size(); i++) {
			List<String> alias = subCommands.get(i).getAlias();
			if (!alias.contains(subCommands.get(i).getName()))
				alias.add(subCommands.get(i).getName());
			for (int j = 0; j < alias.size(); j++) {
				if (args[0].equals(alias.get(j))) {
					String[] SCargs = new String[args.length - 1];
					System.arraycopy(args, 1, SCargs, 0, SCargs.length);
					return subCommands.get(i).addTabCompletionOptions(sender, SCargs, targetPos);
				}
			}
		}
		for (int i = 0; i < subCommands.size(); i++) {
			List<String> alias = subCommands.get(i).getAlias();
			if (!alias.contains(subCommands.get(i).getName()))
				alias.add(subCommands.get(i).getName());
			for (int j = 0; j < alias.size(); j++) {
				ls.add(alias.get(j));
			}
		}
		if (args.length == 1)
			return CommandUtils.getTabCompletion(ls, args);
		return new ArrayList<String>();
	}

	@Override
	public abstract String getCommandUsage(ICommandSender arg0);

	@Override
	public boolean isUsernameIndex(String[] args, int index) {
		return index > 1;
	}

	public void registerSubCommand(SubCommand SubCommand) {
		subCommands.add(SubCommand);
	}

	public void sort() {
		subCommands.sort(SubCommand::compareTo);
	}

	public void unregisterSubCommand(SubCommand SubCommand) {
		subCommands.remove(SubCommand);
	}
}
