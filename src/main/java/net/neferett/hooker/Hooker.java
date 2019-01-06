package net.neferett.hooker;

import lombok.Data;
import lombok.SneakyThrows;
import net.neferett.coreengine.CoreEngine;
import net.neferett.coreengine.Processors.Config.PreConfig;
import net.neferett.coreengine.Processors.Logger.Logger;
import net.neferett.coreengine.Processors.Plugins.ExtendablePlugin;
import net.neferett.coreengine.Processors.Plugins.Plugin;
import net.neferett.hooker.API.HookerAPI;
import net.neferett.hooker.Commands.LoadDatas;
import net.neferett.hooker.Configuration.File;
import net.neferett.hooker.Manager.CityManager;
import net.neferett.hooker.Routes.RouterManagers;
import net.neferett.hooker.Server.ServerHandler;
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
    private ServerHandler serverHandler;
    private RouterManagers managers;

    private void loadConfig() {
        this.preConfig = new PreConfig( this.getConfigPath(), File.class);
        this.file = (File) this.preConfig.loadPath().loadClazz().getConfig();
    }

    public RedisAPI getNewRedisInstance() {
        return new RedisAPI(this.file.getIp(), this.file.getPassword(), this.file.getRedisPort());
    }

    private void loadRoutes() {
        this.managers.buildRoutes();
        this.serverHandler.addRoutes(this.managers.getRoutes());
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

        this.managers = new RouterManagers();
        this.serverHandler = new ServerHandler(this.file.getPort());
        this.serverHandler.build();
        this.loadRoutes();

        this.loadCommands();
        this.createChannel(this.file.getChannel());

        this.manager.setStudentByCity();

        Logger.log("DataBase fully loaded !");

        this.serverHandler.start();
    }

    @Override
    public void onDisable() {
        Logger.logInChannel("ShutDown Server", this.file.getChannel());
        this.serverHandler.stop();
        this.redisAPI.close();
    }

}
