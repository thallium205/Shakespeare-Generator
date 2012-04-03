package main;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.CanReadFileFilter;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.lucene.analysis.SimpleAnalyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.shingle.ShingleAnalyzerWrapper;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.util.Version;

import com.google.code.morphia.Datastore;
import com.google.code.morphia.Morphia;
import com.mongodb.Mongo;
import com.mongodb.MongoException;

public class Shingler
{
	Collection<File> files;
	Mongo mongo;
	Morphia morphia;
	Datastore ds;
	
	private static final Logger LOG = Logger.getLogger(Shingler.class.getName());

	/**
	 * Creates a shingler instance to populate the mongo database.
	 * @param host - The path to the mongo database instance.  ex: localhost
	 * @param name - The name of the mongo database.
	 * @param dir - The directory of where the files are at to create ngrams.
	 */
	public Shingler(String host, String name, String dir)
	{
		files = getFolderContents(dir);
		
		try
		{
			mongo = new Mongo(host);
			morphia = new Morphia();
			ds = morphia.createDatastore(mongo, name);
		} 
		
		catch (UnknownHostException e)
		{
			LOG.log(Level.SEVERE, "Mongo Failed.  Reason: " + e.getMessage(), e);
		} 
		
		catch (MongoException e)
		{
			LOG.log(Level.SEVERE, "Mongo Failed.  Reason: " + e.getMessage(), e);
		}
	}
	
	public Shingler (String host, String name, int port, String dir)
	{
		
	}
	
	public Shingler(String host, String name, String user, String pass, String dir)
	{
		
	}
	
	public Shingler (String host, String name, int port, String user, String pass, String dir)
	{
		
	}	
	
	/**
	 *  Populates the database with n grams
	 * @param min - The minimum n-gram size - must be at least 2.
	 * @param max - The maximum n-gram size.
	 */
	public void execute(int min, int max)
	{
		// Each document will have a ShingleThread parsing it
		ExecutorService shingleExecutor = Executors.newFixedThreadPool(files.size());
		
		for (Iterator<File> iter = files.iterator(); iter.hasNext();)
		{
			shingleExecutor.execute(new ShingleThread(ds, iter.next(), min, max));
		}	
		
		shingleExecutor.shutdown();		
	}	
	
	/**
	 * Returns all the files in a directory.
	 * @param dir - Path to the directory that contains the text documents to be parsed.
	 * @return
	 */
	private Collection<File> getFolderContents(String dir)
	{
		// Collect all readable documents
		File file = new File(dir);
		Collection<File> files = FileUtils.listFiles(file, CanReadFileFilter.CAN_READ, DirectoryFileFilter.DIRECTORY);
		return files;
	}
	
	/**
	 * Persists the ngrams to the database.
	 * @author John
	 *
	 */
	static class ShingleThread implements Runnable
	{
		Datastore ds;
		File file;
		int min;
		int max;
		
		public ShingleThread(Datastore ds, File file, int min, int max)
		{
			this.ds = ds;
			this.file = file;
			this.min = min;
			this.max = max;
		}
		
		@Override
		public void run()
		{
			try
			{			
				FileReader reader = new FileReader(file);
				
				// Parse the file into n-gram tokens
				SimpleAnalyzer simpleAnalyzer = new SimpleAnalyzer(Version.LUCENE_35);			
				ShingleAnalyzerWrapper shingleAnalyzer = new ShingleAnalyzerWrapper(simpleAnalyzer, min, max);
				
				TokenStream stream = shingleAnalyzer.tokenStream("contents", reader);
				CharTermAttribute charTermAttribute = stream.getAttribute(CharTermAttribute.class);

				// Store them in the database
				ArrayList<String> gram = new ArrayList<String>();
				while (stream.incrementToken())
				{					
					Collections.addAll(gram, charTermAttribute.toString().split(" "));	
					ds.save(new Ngram(gram.size(), gram));		
					gram.clear();
				}
				
				LOG.log(Level.INFO, file.getName() + " completed.");
			}

			catch (FileNotFoundException e)
			{
				LOG.log(Level.SEVERE, "Parse Failed.  Reason: " + e.getMessage(), e);
			}

			catch (IOException e)
			{
				LOG.log(Level.SEVERE, "Parse Failed.  Reason: " + e.getMessage(), e);
			}			
		}		
	}
}
