package fr.inria.convecs.sbpmn.files;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;


/*
 * CSVWriter writer = new CSVWriter("usage.csv");
 * writer.write(new String[]{"a", "b", "c"});
 * writer.write(new String[]{"d", "e", "f"});
 */
public class CSVWriter{
	
	private BufferedWriter writer;
	
	public CSVWriter(String writePath) throws UnsupportedEncodingException, FileNotFoundException {
		
		File file = new File(writePath);
		
		if(file.exists()) {
			System.out.println("The file exists");
		}else {
			try {
				file.createNewFile();
				System.out.println("The file created successfully");
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
		
		this.writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF-8"));
	}
	
	public void write(String[] writeStrings) throws IOException {
		int size = writeStrings.length;
		for(int i = 0; i < size - 1; i++) {
			writer.append(writeStrings[i] + ","); 
		}	
		writer.append(writeStrings[size-1]);
		writer.append("\r");
		writer.flush();
	}
}
