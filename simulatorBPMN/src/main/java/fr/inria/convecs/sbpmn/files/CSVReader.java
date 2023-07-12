package fr.inria.convecs.sbpmn.files;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;


/*
 * CSVReader reader = new CSVReader("usage.csv");
 * String str1 = reader.readLine();
 * String str2 = reader.readLine();
 */

public class CSVReader {
	
	
	BufferedReader reader;
	
	public String readline() throws IOException{
		return reader.readLine();
	}
	
	public CSVReader(String pathName) throws FileNotFoundException {
		this.reader = new BufferedReader(new FileReader(new File(pathName)));
	}
}
