package org.twindev.embeds;

import net.dv8tion.jda.api.EmbedBuilder;
import org.twindev.database.SQLiteDataSource;

public class Embeds {

    public static EmbedBuilder getGenerateEmbed(String username, String license, String plugin) {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle("TwinLicense");
        embed.setDescription("Generated a license for " + username + " usable with " + plugin);
        embed.addField("License: ", license, false);
        return embed;
    }

    public static EmbedBuilder getShowEmbed(String username) {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle("TwinLicense");
        embed.setDescription("Showing licenses for " + username);
        embed.addField("Licenses: ", SQLiteDataSource.getUserLicenses(username), false);
        return embed;
    }

    public static EmbedBuilder getDeleteEmbed(String deleteUser, String deletePlugin) {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle("TwinLicense");
        embed.setDescription("Deleted " + deleteUser + "'s license for " + deletePlugin);
        return embed;
    }
}
