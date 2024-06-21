package org.twindev.commands;

import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.UserSnowflake;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.twindev.database.SQLiteDataSource;
import org.twindev.embeds.Embeds;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GenerateLicense extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        String command = event.getName();
        if (command.equals("generate-license")) {
            String userId = event.getOption("name").getAsUser().getName();
            String plugin = event.getOption("plugin").getAsString();
            String license = generateRandomString();
            event.replyEmbeds(Embeds.getGenerateEmbed(userId, license, plugin).build()).queue();
            SQLiteDataSource.setLicense(userId, license, plugin);
        }
        else if (command.equals("show-licenses")) {
            String showUser = event.getOption("showuser").getAsUser().getName();
            if (showUser.isEmpty()) {
                event.reply("Please provide a user to show licenses for").queue();
            } else {
                event.replyEmbeds(Embeds.getShowEmbed(showUser).build()).queue();
            }

        }
        else if (command.equals("delete-license")) {
            String deleteUser = event.getOption("deleteuser").getAsUser().getName();
            String deletePlugin = event.getOption("deleteplugin").getAsString();
            if (deleteUser.isEmpty() || deletePlugin.isEmpty()) {
                event.reply("Please provide a user and plugin to delete a license for").queue();
            } else {
                event.replyEmbeds(Embeds.getDeleteEmbed(deleteUser, deletePlugin).build()).queue();
                SQLiteDataSource.deleteLicense(deleteUser, deletePlugin);
            }
        }

    }

    @Override
    public void onGuildReady(GuildReadyEvent event) {
        List<CommandData> commands = new ArrayList<>();


        // Command: /generate-license <name> <plugin>
        OptionData option_1 = new OptionData(OptionType.USER, "name", "The name of the player", true);
        OptionData option_2 = new OptionData(OptionType.STRING, "plugin", "The plugin to generate a license for", true);
        commands.add(Commands.slash("generate-license", "Generate license for a player").addOptions(option_1, option_2));

        // Command: /show-licenses <name>
        OptionData option_3 = new OptionData(OptionType.USER, "showuser", "The name of the player", true);
        commands.add(Commands.slash("show-licenses", "Show licenses for a player").addOptions(option_3));

        // Command: /delete-license <name> <plugin>
        OptionData option_4 = new OptionData(OptionType.USER, "deleteuser", "The name of the player", true);
        OptionData option_5 = new OptionData(OptionType.STRING, "deleteplugin", "The plugin to delete a license for", true);
        commands.add(Commands.slash("delete-license", "Delete license for a player").addOptions(option_4, option_5));
        event.getGuild().updateCommands().addCommands(commands).queue();
    }

    private static char getRandomCharacter(Random random) {
        String alphanumeric = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        return alphanumeric.charAt(random.nextInt(alphanumeric.length()));
    }

    public static String generateRandomString() {
        Random random = new Random();
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < 4; i++) { // Four groups
            if (i > 0) {
                sb.append('-'); // Add the dash between groups
            }
            for (int j = 0; j < 4; j++) { // Four characters in each group
                sb.append(getRandomCharacter(random));
            }
        }

        return sb.toString();
    }
}
