package musician101.itembank.forge;

import java.io.File;

import musician101.itembank.forge.command.account.AccountCommand;
import musician101.itembank.forge.command.itembank.IBCommand;
import musician101.itembank.forge.config.ConfigHandler;
import musician101.itembank.forge.lib.Constants.ModInfo;
import musician101.itembank.forge.util.Permissions;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(modid=ModInfo.ID, name=ModInfo.NAME, version=ModInfo.VERSION)
public class ItemBank
{
	@Instance(value=ModInfo.ID)
	public static ItemBank instance;
	
	public static Logger logger = LogManager.getLogger(ModInfo.NAME);
	public static Permissions permissions;
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{
		ConfigHandler.init(event.getModConfigurationDirectory());
		FMLCommonHandler.instance().bus().register(new ConfigHandler());
		permissions = new Permissions(new File(event.getModConfigurationDirectory(), "permissions.json"));
		logger.info("Pre-Init complete");
		
		//versionCheck();
	}
	
	
	@EventHandler
	public void onServerStarting(FMLServerStartingEvent event)
	{
		event.registerServerCommand(new IBCommand());
		event.registerServerCommand(new AccountCommand());
	}
	
	/*private void versionCheck()
	{
		if (!config.checkForUpdate())
			getLogger().info("Update check is disabled.");
		else
		{
			Updater updater = new Updater(this, 59073, this.getFile(), UpdateType.NO_DOWNLOAD, true);
			if (updater.getResult() == UpdateResult.UPDATE_AVAILABLE)
				getLogger().info(Messages.UPDATER_NEW + " " + updater.getLatestName());
			else if (updater.getResult() == UpdateResult.NO_UPDATE)
				getLogger().info(Messages.UPDATER_CURRENT + " " + updater.getLatestName());
			else
				getLogger().info(Messages.UPDATER_ERROR);
		}
	}*/
	
	/*public MySQLHandler getMySQLHandler()
	{
		return mysql;
	}
	
	public void setMySQLHandler(MySQLHandler mysql)
	{
		this.mysql = mysql;
	}*/
}
