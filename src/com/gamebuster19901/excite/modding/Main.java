package com.gamebuster19901.excite.modding;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOError;
import java.io.IOException;
import java.io.PrintStream;

import com.gamebuster19901.excite.modding.game.file.toc.TOCFile;

public class Main {
	static {
		Main.class.getClassLoader().setDefaultAssertionStatus(true);
	}
	
	public static final File RUN_DIR = new File("./run");
	public static final File GAME_FILES = new File("./gameData");
	public static final File LOG; 
	public static final PrintStream SYSOUT;
	static {
		try {
			SYSOUT = new PrintStream(new FileOutputStream(new File("./out.txt")));
		} catch (FileNotFoundException e) {
			throw new Error(e);
		}
		//SYSOUT = System.out;
	}
	
	static {
		try {
			LOG = new File(RUN_DIR.getCanonicalPath() + File.pathSeparator + "log.txt");
			LOG.delete();
			LOG.createNewFile();
		}
		catch(IOException e) {
			throw new IOError(e);
		}
	}
	
	public static void main(String[] args) throws IOException {
		File file = null;
		try {
			PrintStream o = new PrintStream(new File(RUN_DIR.getCanonicalPath() + File.pathSeparator + "log.txt"));
			
			System.setOut(o);
			
			if(!RUN_DIR.exists()) {
				RUN_DIR.mkdirs();
				GAME_FILES.mkdirs();
				System.out.println("Run directory created, add the game files to " + GAME_FILES.getCanonicalPath() + " and then run the program again");
			}
			else {
				for(File f : GAME_FILES.listFiles()) {
					file = f;
					if(f.getPath().endsWith(".toc")) {
						new TOCFile(f);
						System.out.println(f + " is valid");
					}
				}
			}
		}
		catch(Throwable t) {
			SYSOUT.println("Error in " + file);
			t.printStackTrace(SYSOUT);
		}
		
	}
}
