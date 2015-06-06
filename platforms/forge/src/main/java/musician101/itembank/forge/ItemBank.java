package musician101.itembank.forge;

import musician101.itembank.forge.command.itembank.IBCommand;
import musician101.itembank.forge.config.ConfigHandler;
import musician101.itembank.forge.lib.Constants.ModInfo;
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
	
	public static Logger log = LogManager.getLogger(ModInfo.NAME);
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{
		ConfigHandler.init(event.getModConfigurationDirectory());
		FMLCommonHandler.instance().bus().register(new ConfigHandler());
		log.info("Pre-Init complete");
		
		//versionCheck();
		
		//getServer().getPluginManager().registerEvents(new InventoryListener(this), this);
		
		//getCommand(Commands.ACCOUNT_CMD).setExecutor(new AccountCommandExecutor(this));
		//getCommand(Commands.ITEMBANK_CMD).setExecutor(new IBCommandExecutor(this));
	}
	
	
	@EventHandler
	public void onServerStarting(FMLServerStartingEvent event)
	{
		event.registerServerCommand(new IBCommand());
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