package com.gamebuster19901.excite.modding;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;

import com.gamebuster19901.excite.modding.game.file.toc.RESArchive;
import com.gamebuster19901.excite.modding.game.file.toc.TOCFile;
import com.gamebuster19901.excite.modding.game.file.toc.TOCFile.Resource;

public class Main {
	static {
		Main.class.getClassLoader().setDefaultAssertionStatus(true);
	}
	
	public static final File RUN_DIR = new File("./run");
	public static final File GAME_FILES = new File("./gameData");
	public static final File LOG; 
	private static final PrintStream CONSOLE = System.out;
	private static final PrintStream LOGOUT;
	static {
		try {
			
			if(!RUN_DIR.exists()) {
				RUN_DIR.mkdirs();
				GAME_FILES.mkdirs();
				System.out.println("Run directory created, add the game files to " + GAME_FILES.getCanonicalPath() + " and then run the program again");
				System.exit(0);
			}
			LOG = new File(RUN_DIR.getCanonicalPath() + File.separator + "log.txt");
			LOG.delete();
			LOG.createNewFile();
			LOGOUT = new PrintStream(new FileOutputStream(LOG));
			PrintStream stdout = new PrintStream(new MultiOutputStream(CONSOLE, LOGOUT));
			System.setOut(stdout);
		} catch (IOException e) {
			throw new Error(e);
		}
	}
	
	
	public static void main(String[] args) {
		ArrayList<TOCFile> tocs = new ArrayList<>();
		try {
			int badTocs = 0;
			
			//File[] gameFiles = GAME_FILES.listFiles();
			File[] gameFiles = new File[] {GAME_FILES.toPath().resolve("Turtle.toc").toFile()};
			if(gameFiles.length == 0) {
				throw new IllegalStateException("No .toc files detected!");
			}
			for(File f : gameFiles) {
				if(f.getPath().endsWith(".toc")) {
					try {
						tocs.add(new TOCFile(f));
						System.out.println(f + " is valid");
					}
					catch(Throwable t) {
						System.err.println("Unable to load toc file " + f.getName());
						System.out.println("Unable to load toc file " + f.getName());
						t.printStackTrace();
						badTocs++;
						if(badTocs < 3) {
							Thread.sleep(3000);
						}
					}
				}
			}
			System.out.println("Checking RES files...");
			Thread.sleep(5000);
			for(TOCFile toc : tocs) {
				RESArchive archive = new RESArchive(toc);
				archive.check();
			}
			System.out.println("Extracting resource files...");
			for(TOCFile toc : tocs) {
				if(new RESArchive(toc).isCompressed()) {
					System.out.println("Skipping " + toc + " - File is compressed!");
					continue;
				}
				else {
					System.out.println("Extracting " + toc);
				}
				File output = new File(RUN_DIR.getCanonicalPath() + "/" + toc.getResourceBundle().getName());
				output.mkdirs();
				for(Resource resource : toc.getResources()) {
					System.out.println(resource.toDebugString());
					File resourceOutput = new File(output.getCanonicalPath() + "/" + resource.getName());
					resourceOutput.createNewFile();
					FileOutputStream fos = new FileOutputStream(resourceOutput);
					fos.write(resource.toResourceBytes());
					fos.close();
				}
			}
		}
		catch(Throwable t) {
			t.printStackTrace();
		}
		System.out.println("\nTerminated");
	}
	
	/*
	public static void main(String[] args) throws IOException {
		try {
			PrintStream o = new PrintStream(new File(RUN_DIR.getCanonicalPath() + File.separator + "log.txt"));
			
			System.setOut(o);
			System.setErr(o);
			
			int badTocs = 0;
			
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
						System.err.println("Unable to load toc file " + f.getName());
						System.out.println("Unable to load toc file " + f.getName());
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
			
			System.out.println(ResourceFiles.resourceDetails.size() + " resources detected... preparing them now...");
			
			int i = 0;
			for(ResourceDetails resourceDetail : ResourceFiles.resourceDetails) {
				if(resourceDetail.toc.resourceBundle != null) {
					try {
						new Resource(resourceDetail);
						System.out.println("[" + (int)((((double)++i / (double)amount)) * 100) + "%] - Prepared " + resourceDetail.toc + "/" + resourceDetail.getName());
					}
					catch (Throwable t) {
						String message = "Unable to prepare " + resourceDetail.toc + "/" + resourceDetail.getName();
						System.out.println(message);
						System.err.println(message);
						t.printStackTrace(CONSOLE);
						t.printStackTrace(System.err);
					}
				}
			}
			
			if(i != amount) {
				String message = "[WARNING] - Unable to prepare " + (amount - i) + " resources ";
				System.out.println(message);
				System.err.println(message);
			}
			
			{
				String message = "Prepared " + i + "/" + amount + " resources for extraction.";
				if(badTocs > 0) {
					message = "[WARNING] - " + message + " (" + badTocs + " bad toc file[s], check logs!)";
				}
				System.out.println("\n" + message);
				Thread.sleep(1500);
				System.out.println();
				Thread.sleep(1500);
			}
			
			int preparedAmount = ResourceFiles.resources.size();
			
			if(preparedAmount == 0) {
				throw new IllegalStateException("No resources were prepared!");
			}
			
			System.out.println(ResourceFiles.resources.size() + " resources successfully prepared, extracting them now...");
			
			Thread.sleep(1500);
			
			System.out.println();
			
			int extracted = 0;
			
			for(Resource resource : ResourceFiles.resources) {
				try {
					resource.extract(RUN_DIR);
					System.out.println("[" + (int)((((double)++extracted / (double)preparedAmount)) * 100) + "%] - Extracted " + resource);
				}
				catch(Throwable t) {
					String message = "[WARNING] - Unable to extract " + resource;
					System.out.println(message);
					t.printStackTrace(CONSOLE);
					System.err.println(message);
					t.printStackTrace(System.err);
				}
			}
			
			if(extracted == 0) {
				throw new IllegalStateException("Extracted 0 resources!");
			}
			
			System.out.println("\nExtracted " + extracted + "/" +  preparedAmount + "/" + amount + " resources");
			Thread.sleep(1500);
			if(extracted != amount) {
				String message = "\n[WARNING] - Unable to extract " + (amount - extracted) + " total detected resources";
				System.out.println(message);
				System.err.println(message);
			}
			Thread.sleep(1500);
		}
		catch(Throwable t) {
			t.printStackTrace(CONSOLE);
		}
		System.out.println("\nTerminated");
	}
	*/
}
