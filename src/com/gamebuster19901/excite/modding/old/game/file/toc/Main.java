package com.gamebuster19901.excite.modding.old.game.file.toc;

import java.io.File;
import java.io.IOError;
import java.io.IOException;
import java.io.PrintStream;

public class Main {

	static {
		Main.class.getClassLoader().setDefaultAssertionStatus(true);
	}
	
	public static final File RUN_DIR = new File("./run");
	public static final File GAME_FILES = new File("./gameData");
	public static final File LOG; 
	public static final PrintStream SYSOUT = System.out;
	
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
					if(f.getPath().endsWith(".toc")) {
						new TOCFile(f);
					}
				}
			}
		}
		catch(Throwable t) {
			t.printStackTrace(SYSOUT);
		}
		
	}
	
}
