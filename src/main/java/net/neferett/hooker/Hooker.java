package net.neferett.hooker;

import lombok.Data;
import net.neferett.coreengine.CoreEngine;
import net.neferett.coreengine.Processors.Config.PreConfig;
import net.neferett.coreengine.Processors.Logger.Logger;
import net.neferett.coreengine.Processors.Plugins.ExtendablePlugin;
import net.neferett.coreengine.Processors.Plugins.Plugin;
import net.neferett.hooker.API.HookerAPI;
import net.neferett.hooker.Commands.LoadDatas;
import net.neferett.hooker.Configuration.File;
import net.neferett.hooker.Manager.CityManager;
import net.neferett.httpserver.api.HTTPServerAPI;
import net.neferett.httpserver.api.Types.HttpTypes;
import net.neferett.redisapi.RedisAPI;

@Data
@Plugin(name = "Hooker", configPath = "Hooker/config.json")
public class Hooker extends ExtendablePlugin {

    public static Hooker getInstance() {
        return (Hooker) CoreEngine.getInstance().getPlugin(Hooker.class);
    }

    private RedisAPI redisAPI;
    private HookerAPI api;
    private CityManager manager;

    private PreConfig preConfig;
    private File file;

    private HTTPServerAPI serverAPI;

    private void loadConfig() {
        this.preConfig = new PreConfig( this.getConfigPath(), File.class);
        this.file = (File) this.preConfig.loadPath().loadClazz().getConfig();
    }

    public RedisAPI getNewRedisInstance() {
        return new RedisAPI(this.file.getIp(), this.file.getPassword(), this.file.getRedisPort());
    }

    private void loadCommands() {
        this.addCommand(LoadDatas.class);
    }

    @Override
    public void onEnable() {
        this.api = new HookerAPI("https://intra.epitech.eu/auth-948d80eeb89899ed2988307519d86b9383d72629");
        this.manager = new CityManager(this.api);

        this.loadConfig();

        this.redisAPI = this.getNewRedisInstance();

        this.serverAPI = new HTTPServerAPI(this.file.getPort(), this.getEngine().getConfig().getThreads(), HttpTypes.HTTPS, "password", "testkey");

        this.file.getRoutes().forEach(e -> this.serverAPI.addAllRoutesInPath("net.neferett.hooker.Routes", e));

        this.loadCommands();
        this.createChannel(this.file.getChannel());

        this.manager.setStudentByCity();

        Logger.log("DataBase fully loaded !");

        this.serverAPI.start();
    }

    @Override
    public void onDisable() {
        Logger.logInChannel("ShutDown Server", this.file.getChannel());
        this.redisAPI.close();
        this.serverAPI.stop();
    }

}
