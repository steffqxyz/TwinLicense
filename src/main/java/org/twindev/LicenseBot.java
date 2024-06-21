package org.twindev;

import io.github.cdimascio.dotenv.Dotenv;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.api.sharding.ShardManager;
import org.twindev.commands.GenerateLicense;
import org.twindev.database.SQLiteDataSource;

import java.sql.SQLException;

public class LicenseBot {

    private final ShardManager shardManager;
    private final Dotenv config;

    public LicenseBot() throws SQLException {
        SQLiteDataSource.getConnection();
        config = Dotenv.configure().load();

        DefaultShardManagerBuilder builder = DefaultShardManagerBuilder.createDefault(config.get("TOKEN"));
        builder.setStatus(OnlineStatus.ONLINE);
        builder.setActivity(Activity.watching("Licenses"));
        builder.enableIntents(GatewayIntent.GUILD_MEMBERS);
        shardManager = builder.build();

        shardManager.addEventListener(new GenerateLicense());
    }

    public boolean checkLicense(String license, String plugin) {
        return SQLiteDataSource.checkLicense(license, plugin);
    }

    public ShardManager getShardManager() {
        return shardManager;
    }

    public Dotenv getConfig() {
        return config;
    }

    public static void main(String[] args) throws SQLException {
        LicenseBot bot = new LicenseBot();

    }
}