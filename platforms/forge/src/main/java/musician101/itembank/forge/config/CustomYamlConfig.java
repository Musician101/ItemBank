package musician101.itembank.forge.config;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;
import java.util.logging.Level;

import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.file.YamlConstructor;
import org.bukkit.configuration.file.YamlRepresenter;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.DumperOptions.FlowStyle;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.emitter.ScalarAnalysis;
import org.yaml.snakeyaml.error.YAMLException;
import org.yaml.snakeyaml.representer.Representer;

public class CustomYamlConfig extends YamlConfiguration
{
	private final DumperOptions options = new CustomDumperOptions();
	private final Representer rep = new YamlRepresenter();
	private final Yaml yaml = new Yaml(new YamlConstructor(), rep, options);
	
	@Override
	public String saveToString()
	{
		options.setIndent(options().indent());
		options.setDefaultFlowStyle(FlowStyle.BLOCK);
		rep.setDefaultFlowStyle(FlowStyle.BLOCK);
		String header = buildHeader();
		String dump = yaml.dump(getValues(false));
		if (dump.equals(BLANK_CONFIG))
			dump = "";
		
		return header + dump;
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	public void loadFromString(String contents) throws InvalidConfigurationException
	{
		Validate.notNull(contents, "Contents cannot be null");
		Map input;
		try
		{
			input = (Map) yaml.load(contents);
		}
		catch (YAMLException e)
		{
			throw new InvalidConfigurationException(e);
		}
		catch (ClassCastException e)
		{
			throw new InvalidConfigurationException("Top level is not a Map.");
		}
		
		String header = parseHeader(contents);
		if (header.length() > 0)
			options().header(header);
		
		if (input != null)
			convertMapsToSections(input, this);
	}
	
	public static CustomYamlConfig loadConfiguration(File file)
	{
		Validate.notNull(file, "File cannot be null");
		CustomYamlConfig config = new CustomYamlConfig();
		try
		{
			config.load(file);
		}
		catch (FileNotFoundException e){}
		catch (IOException e)
		{
			Bukkit.getLogger().log(Level.SEVERE, "Cannot load " + file, e);
		}
		catch (InvalidConfigurationException e)
		{
			Bukkit.getLogger().log(Level.SEVERE, "Cannot load " + file, e);
		}
		
		return config;
	}
	
	private class CustomDumperOptions extends DumperOptions
	{
		@SuppressWarnings("deprecation")
		@Override
		public ScalarStyle calculateScalarStyle(ScalarAnalysis analysis, ScalarStyle style)
		{
			if (analysis.scalar.contains("\n") || analysis.scalar.contains("\r"))
				return ScalarStyle.LITERAL;
			else
				return super.calculateScalarStyle(analysis, style);
		}
	}
}
