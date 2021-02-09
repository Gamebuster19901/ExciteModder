package com.gamebuster19901.excite.modding;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import com.gamebuster19901.excite.modding.game.file.toc.TOCFile;
import com.gamebuster19901.excite.modding.game.file.toc.ResourceFiles;
import com.gamebuster19901.excite.modding.game.file.toc.ResourceFiles.Resource;
import com.gamebuster19901.excite.modding.game.file.toc.ResourceFiles.ResourceDetails;

public class Main {
	static {
		Main.class.getClassLoader().setDefaultAssertionStatus(true);
	}
	
	public static final File RUN_DIR = new File("./run");
	public static final File GAME_FILES = new File("./gameData");
	public static final File LOG; 
	public static final PrintStream CONSOLE = System.out;
	public static final PrintStream SYSOUT;
	static {
		try {
			SYSOUT = new PrintStream(new FileOutputStream(new File("./out.txt")));
			LOG = new File(RUN_DIR.getCanonicalPath() + File.pathSeparator + "log.txt");
			LOG.delete();
			LOG.createNewFile();
		} catch (IOException e) {
			throw new Error(e);
		}
	}
	
	public static void main(String[] args) throws IOException {
		try {
			PrintStream o = new PrintStream(new File(RUN_DIR.getCanonicalPath() + File.pathSeparator + "log.txt"));
			
			System.setOut(o);
			
			int badTocs = 0;
			
			if(!RUN_DIR.exists()) {
				RUN_DIR.mkdirs();
				GAME_FILES.mkdirs();
				System.out.println("Run directory created, add the game files to " + GAME_FILES.getCanonicalPath() + " and then run the program again");
			}
			else {
				File[] gameFiles = GAME_FILES.listFiles();
				if(gameFiles.length == 0) {
					throw new IllegalStateException("No .toc files detected!");
				}
				for(File f : GAME_FILES.listFiles()) {
					if(f.getPath().endsWith(".toc")) {
						try {
							new TOCFile(f);
							System.out.println(f + " is valid");
						}
						catch(Throwable t) {
							System.out.println("Unable to load toc file " + f.getName());
							CONSOLE.println("Unable to load toc file " + f.getName());
							t.printStackTrace(System.out);
							t.printStackTrace(CONSOLE);
							badTocs++;
							if(badTocs < 3) {
								Thread.sleep(3000);
							}
						}
					}
				}
				
				int amount = ResourceFiles.resourceDetails.size();
				
				if(amount == 0) {
					throw new IllegalStateException("No resources detected!");
				}
				
				CONSOLE.println(ResourceFiles.resourceDetails.size() + " resources detected... preparing them now...");
				
				int i = 0;
				for(ResourceDetails resourceDetail : ResourceFiles.resourceDetails) {
					if(resourceDetail.toc.resourceBundle != null) {
						try {
							new Resource(resourceDetail);
							CONSOLE.println("[" + (int)((((double)++i / (double)amount)) * 100) + "%] - Prepared " + resourceDetail.toc + "/" + resourceDetail.getName());
						}
						catch (Throwable t) {
							String message = "Unable to prepare " + resourceDetail.toc + "/" + resourceDetail.getName();
							CONSOLE.println(message);
							System.err.println(message);
							t.printStackTrace(CONSOLE);
							t.printStackTrace(System.err);
						}
					}
				}
				
				if(i != amount) {
					String message = "[WARNING] - Unable to prepare " + (amount - i) + " resources ";
					CONSOLE.println(message);
					System.err.println(message);
				}
				
				String message = "Prepared " + i + "/" + amount + " resources for extraction.";
				if(badTocs > 0) {
					message = "[WARNING] - " + message + " (" + badTocs + " bad toc file[s], check logs!)";
				}
				CONSOLE.println(message);
				Thread.sleep(3000);
				
				int preparedAmount = ResourceFiles.resources.size();
				
				if(preparedAmount == 0) {
					throw new IllegalStateException("No resources were prepared!");
				}
				
				CONSOLE.println(ResourceFiles.resources.size() + " resources successfully prepared, extracting them now...");
				
			}
		}
		catch(Throwable t) {
			t.printStackTrace(CONSOLE);
		}
		CONSOLE.println("Terminated");
	}
}
