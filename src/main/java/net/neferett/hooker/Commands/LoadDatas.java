package net.neferett.hooker.Commands;

import net.neferett.coreengine.Processors.Logger.Logger;
import net.neferett.coreengine.Processors.Plugins.Commands.Command;
import net.neferett.coreengine.Processors.Plugins.Commands.ExtendableCommand;
import net.neferett.hooker.Hooker;
import net.neferett.hooker.Manager.CityManager;

@Command(name = "update", argsLength = 0, desc = "Refresh all datas")
public class LoadDatas extends ExtendableCommand {

    @Override
    public boolean onCommand(String... strings) {

        CityManager manager = Hooker.getInstance().getManager();

        manager.loadCities();

        manager.setStudentByCity();

        Logger.logInChannel(Hooker.getInstance().getFile().getChannel(), "Done.");

        return false;
    }
}

