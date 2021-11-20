package fr.atesab.act.utils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import fr.atesab.act.ACTMod;
import fr.atesab.act.internalcommand.InternalCommandModule;
import fr.atesab.act.internalcommand.InternalCommand;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.Registry;
import net.minecraftforge.event.ForgeEventFactory;

/**
 * A set of tools to help to create commands
 * 
 * @author ATE47
 * @since 2.0
 */
@InternalCommandModule(name = "command")
public class CommandUtils {
	private static final Random RANDOM = ACTMod.RANDOM;

	public static <T> T getRandomElement(T[] array) {
		return array[RANDOM.nextInt(array.length)];
	}

	/**
	 * Get all visible players in tab
	 * 
	 * @return players' names
	 * @since 2.0
	 */
	@InternalCommand(name = "playerlist")
	public static List<String> getPlayerList() {
		List<PlayerInfo> networkPlayerInfos = new ArrayList<>(
				Minecraft.getInstance().player.connection.getOnlinePlayers());
		networkPlayerInfos.sort((o1, o2) -> o1.getGameMode().getName().compareToIgnoreCase(o2.getGameMode().getName()));
		List<String> players = new ArrayList<String>();
		for (PlayerInfo info : networkPlayerInfos)
			players.add(info.getGameMode().getName());
		return players;
	}

	/**
	 * Get all sub completion argument
	 * 
	 * @param options Possible sub arguments
	 * @param args    command arguments
	 * @return List of string who match sorted by name
	 * @since 2.0
	 * @deprecated
	 */
	@Deprecated
	public static List<String> getTabCompletion(List<String> options, String[] args) {
		List<String> optionsEnd = new ArrayList<>();
		if (args.length == 0)
			return optionsEnd;
		String start = args[args.length - 1].toLowerCase();
		for (int i = 0; i < options.size(); i++) {
			if (options.get(i).toLowerCase().startsWith(start.toLowerCase()))
				optionsEnd.add(options.get(i));
		}
		optionsEnd.sort(String::compareToIgnoreCase); // sort by name
		return optionsEnd;
	}

	/**
	 * Send a message in chat like with the client (Client command will be execute)
	 * 
	 * @param message chat message to send
	 * @since 2.0
	 */
	@InternalCommand(name = "send")
	public static void sendMessage(String message) {
		LocalPlayer p;
		if ((p = Minecraft.getInstance().player) != null) {
			if (ForgeEventFactory.onClientSendMessage(message).isEmpty())
				return;
			p.chat(message);
		}
	}

}
