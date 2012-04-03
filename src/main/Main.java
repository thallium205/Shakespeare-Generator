package main;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class Main
{

	/**
	 * Creates n-grams from text documents and uses them for various things.
	 * @param args - See help
	 */
	public static void main(String[] args)
	{		
		// Print options
		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp("java -jar shakespeare.jar", getOptions());
		
		// Parse options
		CommandLineParser parser = new GnuParser();
		try
		{
			CommandLine line = parser.parse(getOptions(), args);
			String dir = line.getOptionValue("dir");
			String host = line.getOptionValue("host");
			String name = line.getOptionValue("name");
			int min = Integer.parseInt(line.getOptionValue("min"));
			int max = Integer.parseInt(line.getOptionValue("max"));
			
			// Optional values
			int port = -1;
			String user, pass;
			if (line.hasOption("port"))
				port = Integer.parseInt(line.getOptionValue("port"));
			if (line.hasOption("user"))
				user = line.getOptionValue("user");
			if (line.hasOption("pass"))
				pass = line.getOptionValue("user");
			
			// Create Shingler TODO
			Shingler shingler = new Shingler(host, name, dir);
			shingler.execute(min, max);		
		} 
		
		catch (ParseException e)
		{
			System.err.println( "Parsing failed.  Reason: " + e.getMessage());
		}		
	}
	
	/**
	 * Creates the options menu
	 * @return
	 */
	private static Options getOptions()
	{
		// Define options
		Option dir = OptionBuilder.withArgName("path").hasArg().withDescription("The directory where the literature is stored.").isRequired().create("dir");
		Option host = OptionBuilder.hasArg().withArgName("hostname").withDescription("Hostname of the running mongodb instance.").isRequired().create("host");
		Option name = OptionBuilder.hasArg().withArgName("database").withDescription("Name of the database.").isRequired().create("name");
		Option port = OptionBuilder.hasArg().withArgName("number").withDescription("Port number of the running mongodb instance.").create("port");
		Option user = OptionBuilder.hasArg().withArgName("username").withDescription("Username of the running mongodb instance.").create("user");
		Option pass = OptionBuilder.hasArg().withArgName("password").withDescription("Password of the running mongodb instance.").create("pass");
		Option min = OptionBuilder.hasArg().withArgName("minimum").withDescription("Specify the minimum size of n-grams to generate.").isRequired().create("min");
		Option max = OptionBuilder.hasArg().withArgName("maximum").withDescription("Specify the maximum size of n-grams to generate.").isRequired().create("max");
		
		Options options = new Options();
		options.addOption(dir);
		options.addOption(host);
		options.addOption(name);
		options.addOption(port);
		options.addOption(user);
		options.addOption(pass);
		options.addOption(min);
		options.addOption(max);
		
		return options;		
	}	

}
