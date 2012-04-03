package main;

import java.util.ArrayList;

import com.google.code.morphia.annotations.Embedded;
import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.annotations.Id;

@Entity
public class Ngram
{
	@Id
	private String id;	
	
	private int n;
	@Embedded
	private ArrayList<String> gram;
	
	Ngram()
	{
		
	}
	
	public Ngram(int n, ArrayList<String> gram)
	{
		this.n = n;
		this.gram = gram;
	}
	
	public String getId()
	{
		return id;
	}
	public void setId(String id)
	{
		this.id = id;
	}
	public int getN()
	{
		return n;
	}
	public void setN(int n)
	{
		this.n = n;
	}
	public ArrayList<String> getGram()
	{
		return gram;
	}
	public void setGram(ArrayList<String> gram)
	{
		this.gram = gram;
	}	
	
}
