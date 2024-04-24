package com.gamebuster19901.excite.modding.ui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Insets;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.io.File;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Iterator;

import javax.swing.JFrame;
import com.gamebuster19901.excite.modding.concurrent.BatchListener;
import com.gamebuster19901.excite.modding.concurrent.BatchRunner;
import com.gamebuster19901.excite.modding.concurrent.Batcher;
import com.gamebuster19901.excite.modding.util.SplitOutputStream;
import com.gamebuster19901.excite.modding.concurrent.Batch;
import com.gamebuster19901.excite.modding.concurrent.Batch.BatchedCallable;
import com.gamebuster19901.excite.modding.concurrent.BatchContainer;

import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.UIManager;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JSeparator;
import javax.swing.JTabbedPane;
import javax.swing.JSlider;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.JPanel;
import java.awt.GridLayout;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import javax.swing.JTable;
import javax.swing.JTextArea;

public class Window implements BatchListener, MouseWheelListener {

	static {
		UIManager.getDefaults().put("TabbedPane.contentBorderInsets", new Insets(0,0,0,0));
	}
	
	private JFrame frame;
	private JTextField textFieldDestDir;
	private JTextField textFieldSourceDir;
	private JSlider threadSlider;
	private JLabel lblThreads;
	private static Window window;
	private final JProgressBar progressBar = new JProgressBar();
	private final JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
	private final FixedCellGrid gridPanel = genGridPanel();
	private JTable table;
	
	private BatchRunner<Void> copyOperations;
	private BatchRunner processOperations;


	/**
	 * @wbp.parser.entryPoint
	 */
	public static void main(String[] args) throws InterruptedException {

		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Window window = new Window();
					Window.window = window;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 * @throws InterruptedException 
	 */
	public Window() throws InterruptedException {
		initialize();
		update();
	}

	/**
	 * @wbp.parser.entryPoint
	 */
	private void initialize() throws InterruptedException {
		
		copyOperations = genCopyBatches(null);
		setupFrame();
	}
	
	private void setupFrame() {
		frame = new JFrame(); // @wbp.parser.preferredRoot
		frame.setTitle("ExciteModder");
		frame.setBounds(100, 100, 1000, 680);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		frame.setResizable(false);
		frame.setVisible(true);
		
		JLabel lblUnmoddedDirectory = new JLabel("Source Directory:");
		lblUnmoddedDirectory.setToolTipText("The directory of the unmodified, original ripped files.");
		lblUnmoddedDirectory.setBounds(12, 12, 165, 15);
		frame.getContentPane().add(lblUnmoddedDirectory);
		
		JLabel lblModdedDirectory = new JLabel("Destination Directory:");
		lblModdedDirectory.setToolTipText("Where ExciteModder will copy modified game files to.");
		lblModdedDirectory.setBounds(12, 39, 165, 15);
		frame.getContentPane().add(lblModdedDirectory);
		
		textFieldSourceDir = new JTextField();
		textFieldSourceDir.setColumns(10);
		textFieldSourceDir.setBounds(171, 10, 578, 19);
		frame.getContentPane().add(textFieldSourceDir);
		
		textFieldDestDir = new JTextField();
		textFieldDestDir.setBounds(171, 37, 578, 19);
		frame.getContentPane().add(textFieldDestDir);
		textFieldDestDir.setColumns(10);
		
		JButton btnChangeSource = new JButton("Change");
		btnChangeSource.setToolTipText("Change where ExciteModder will copy the game files from");
		btnChangeSource.setBounds(761, 9, 117, 19);
		btnChangeSource.addActionListener((e) -> {
			File f;
			Path path = Path.of(textFieldSourceDir.getText().trim()).toAbsolutePath();
			if(Files.exists(path)) {
				f = path.toAbsolutePath().toFile();
			}
			else {
				f = null;
			}
			
			JFileChooser chooser = new JFileChooser(f);
			chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			int status = chooser.showOpenDialog(frame);
			if(status == JFileChooser.APPROVE_OPTION) {
				try {
					selectSourceDirectory(chooser.getSelectedFile());
				} catch (InvocationTargetException | InterruptedException e1) {
					e1.printStackTrace();
				}
			}
		});
		frame.getContentPane().add(btnChangeSource);
		
		JButton btnChangeDest = new JButton("Change");
		btnChangeDest.setToolTipText("Change where ExciteModder will copy the game files to");
		btnChangeDest.setBounds(761, 36, 117, 19);
		frame.getContentPane().add(btnChangeDest);
		
		threadSlider = new JSlider();
		threadSlider.getSnapToTicks();
		threadSlider.setMinimum(1);
		threadSlider.setMaximum(Runtime.getRuntime().availableProcessors());
		threadSlider.setValue(Math.max(1, Runtime.getRuntime().availableProcessors() / 2));
		threadSlider.setBounds(5, 90, 92, 16);
		threadSlider.addChangeListener((s) -> {
			if(lblThreads != null) {
				lblThreads.setText("Threads: " + threadSlider.getValue());
			}
		});
		frame.getContentPane().add(threadSlider);
		
		lblThreads = new JLabel("Threads: " + threadSlider.getValue());
		lblThreads.setBounds(12, 74, 132, 15);
		frame.getContentPane().add(lblThreads);
		
		JSeparator separator = new JSeparator();
		separator.setBounds(12, 64, 971, 2);
		frame.getContentPane().add(separator);
		
		progressBar.setVisible(false);
		
		JLabel lblStatus = new JLabel("Progress: 0%");
		lblStatus.setHorizontalAlignment(SwingConstants.CENTER);
		lblStatus.setBounds(440, 110, 120, 15);
		lblStatus.setVisible(false);
		
		frame.getContentPane().add(lblStatus);
		progressBar.setBounds(12, 110, 971, 15);
		frame.getContentPane().add(progressBar);
		
		JButton btnExtract = new JButton("Extract!");
		btnExtract.setBounds(890, 9, 93, 45);
		btnExtract.setEnabled(false);
		
		frame.getContentPane().add(btnExtract);
		frame.validate();
		frame.repaint();
		//tabbedPane = setupTabbedPane(true);
		//frame.add(setupTabbedPane(true)); //this line is necessary here so that the wbp parser can see that the tabbed pane is a subcomponent of the frame. It doesn't seem to recognize it as being so if it isn't manually added here, even though it's added in setupTabbedPane
	}
	
	private void selectSourceDirectory(File selectedDir) throws InvocationTargetException, InterruptedException {
		if(selectedDir != null) {
			textFieldSourceDir.setText(selectedDir.getAbsolutePath());
		}
		else {
			textFieldSourceDir.setText("");
		}
		
		copyOperations.shutdownNow();
		copyOperations = genCopyBatches(selectedDir);
		setupTabbedPane(false);
		update();
	}

	private void setupTabbedPane(boolean initialSetup) {
		tabbedPane.removeAll();
		if(initialSetup) {
			tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
			tabbedPane.setBounds(0, 129, 1000, 511);
			frame.getContentPane().add(tabbedPane);
		}
		
		setupConsoleOutputTab();
		setupStatusTab();
		setupProgressTab();
		
		for(Batcher<Void> b : copyOperations.getBatches()) {
			tabbedPane.addTab(b.getName(), null);
		}
		return;
	}
	
	private void setupConsoleOutputTab() {
		JPanel consolePanel = new JPanel();
		consolePanel.setLayout(new GridLayout(0, 2, 0, 0));
		JTextArea textArea = new JTextArea();
		textArea.setBorder(BorderFactory.createLoweredBevelBorder());
		textArea.setOpaque(true);
		JTextAreaOutputStream textPaneOutputStream = new JTextAreaOutputStream(textArea);
		System.setOut(new PrintStream(SplitOutputStream.splitSysOut(textPaneOutputStream)));
		System.setErr(new PrintStream(SplitOutputStream.splitErrOut(textPaneOutputStream)));
		
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		new Throwable().printStackTrace(pw);
		textArea.setText(sw.toString());
		
		JScrollPane scrollPane = new JScrollPane(textArea);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		tabbedPane.addTab("Console OutputT", null, scrollPane, null);
		
		

	}
	
	public void setupStatusTab() {
		tabbedPane.addTab("Status", null, gridPanel, null);
		Iterator<Batcher<Void>> batches = copyOperations.getBatches().iterator();
		int i = 0;
		while(batches.hasNext()) {
			gridPanel.putComponent(i, new BatchOperationComponent(batches.next()));
			i++;
		}
	}
	
	public void setupProgressTab() {
		JPanel progressPanel = new JPanel();
		tabbedPane.addTab("Progress", null, progressPanel, null);
		
		progressPanel.setLayout(new GridLayout(0, 2, 0, 0));
		setupLeftProgressPane(progressPanel);
		setupRightProgressPane(progressPanel);
		

	}
	
	private void setupLeftProgressPane(JPanel progressPanel) {
		JPanel leftPanel = new JPanel();
		progressPanel.add(leftPanel);
		GridBagLayout gbl_leftPanel = new GridBagLayout();
		gbl_leftPanel.columnWidths = new int[] {100, 0, 70, 90, 0, 0, 0};
		gbl_leftPanel.rowHeights = new int[] {0, 15, 0, 0, 0, 0, 30, 0, 0, 0, 0, 0, 0};
		gbl_leftPanel.columnWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		gbl_leftPanel.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		leftPanel.setLayout(gbl_leftPanel);
		
		JLabel lblCopyOperation = new JLabel("Copy Operation");
		GridBagConstraints gbc_lblCopyOperation = new GridBagConstraints();
		gbc_lblCopyOperation.gridwidth = 6;
		gbc_lblCopyOperation.insets = new Insets(0, 0, 5, 5);
		gbc_lblCopyOperation.gridx = 0;
		gbc_lblCopyOperation.gridy = 0;
		leftPanel.add(lblCopyOperation, gbc_lblCopyOperation);
		BatchOperationComponent allBatchesCopy = new BatchOperationComponent(copyOperations);
		allBatchesCopy.setToolTipText("All Batches");
		GridBagConstraints gbc_allBatches = new GridBagConstraints();
		gbc_allBatches.fill = GridBagConstraints.BOTH;
		gbc_allBatches.gridheight = 11;
		gbc_allBatches.insets = new Insets(0, 0, 0, 0);
		gbc_allBatches.gridx = 0;
		gbc_allBatches.gridy = 1;
		leftPanel.add(allBatchesCopy, gbc_allBatches);
		
		JSeparator separator_1 = new JSeparator();
		GridBagConstraints gbc_separator_1 = new GridBagConstraints();
		gbc_separator_1.fill = GridBagConstraints.BOTH;
		gbc_separator_1.gridheight = 10;
		gbc_separator_1.insets = new Insets(0, 0, 5, 5);
		gbc_separator_1.gridx = 1;
		gbc_separator_1.gridy = 0;
		leftPanel.add(separator_1, gbc_separator_1);
		
		JLabel lblTotalArchives = new JLabel("Total Archives:");
		lblTotalArchives.setHorizontalAlignment(SwingConstants.CENTER);
		GridBagConstraints gbc_lblTotalArchives = new GridBagConstraints();
		gbc_lblTotalArchives.insets = new Insets(0, 0, 5, 5);
		gbc_lblTotalArchives.anchor = GridBagConstraints.NORTHEAST;
		gbc_lblTotalArchives.gridx = 2;
		gbc_lblTotalArchives.gridy = 1;
		leftPanel.add(lblTotalArchives, gbc_lblTotalArchives);
		
		JLabel lblTotalArchivesCount = new JLabel("0");
		GridBagConstraints gbc_lblTotalArchivesCount = new GridBagConstraints();
		gbc_lblTotalArchivesCount.anchor = GridBagConstraints.WEST;
		gbc_lblTotalArchivesCount.insets = new Insets(0, 0, 5, 5);
		gbc_lblTotalArchivesCount.gridx = 3;
		gbc_lblTotalArchivesCount.gridy = 1;
		leftPanel.add(lblTotalArchivesCount, gbc_lblTotalArchivesCount);
		
		JLabel lblFoundResources = new JLabel("Total Resources:");
		lblFoundResources.setHorizontalAlignment(SwingConstants.RIGHT);
		GridBagConstraints gbc_lblFoundResources = new GridBagConstraints();
		gbc_lblFoundResources.anchor = GridBagConstraints.EAST;
		gbc_lblFoundResources.insets = new Insets(0, 0, 5, 5);
		gbc_lblFoundResources.gridx = 2;
		gbc_lblFoundResources.gridy = 2;
		leftPanel.add(lblFoundResources, gbc_lblFoundResources);
		
		JLabel lblTotalResourcesCount = new JLabel("0");
		GridBagConstraints gbc_lblTotalResourcesCount = new GridBagConstraints();
		gbc_lblTotalResourcesCount.anchor = GridBagConstraints.WEST;
		gbc_lblTotalResourcesCount.insets = new Insets(0, 0, 5, 5);
		gbc_lblTotalResourcesCount.gridx = 3;
		gbc_lblTotalResourcesCount.gridy = 2;
		leftPanel.add(lblTotalResourcesCount, gbc_lblTotalResourcesCount);
		
		JLabel lblArchivesCopied = new JLabel("Archives Copied:");
		GridBagConstraints gbc_lblArchivesCopied = new GridBagConstraints();
		gbc_lblArchivesCopied.anchor = GridBagConstraints.EAST;
		gbc_lblArchivesCopied.insets = new Insets(0, 0, 5, 5);
		gbc_lblArchivesCopied.gridx = 2;
		gbc_lblArchivesCopied.gridy = 4;
		leftPanel.add(lblArchivesCopied, gbc_lblArchivesCopied);
		
		JLabel lblArchivesCopiedCount = new JLabel("0");
		GridBagConstraints gbc_lblArchivesCopiedCount = new GridBagConstraints();
		gbc_lblArchivesCopiedCount.anchor = GridBagConstraints.WEST;
		gbc_lblArchivesCopiedCount.insets = new Insets(0, 0, 5, 5);
		gbc_lblArchivesCopiedCount.gridx = 3;
		gbc_lblArchivesCopiedCount.gridy = 4;
		leftPanel.add(lblArchivesCopiedCount, gbc_lblArchivesCopiedCount);
		
		JLabel lblArchivesCopiedPercent = new JLabel("0%");
		GridBagConstraints gbc_lblArchivesCopiedPercent = new GridBagConstraints();
		gbc_lblArchivesCopiedPercent.insets = new Insets(0, 0, 5, 5);
		gbc_lblArchivesCopiedPercent.gridx = 4;
		gbc_lblArchivesCopiedPercent.gridy = 4;
		leftPanel.add(lblArchivesCopiedPercent, gbc_lblArchivesCopiedPercent);
		
		JLabel lblResourcesCopied = new JLabel("Resources Copied:");
		GridBagConstraints gbc_lblResourcesCopied = new GridBagConstraints();
		gbc_lblResourcesCopied.anchor = GridBagConstraints.EAST;
		gbc_lblResourcesCopied.insets = new Insets(0, 0, 5, 5);
		gbc_lblResourcesCopied.gridx = 2;
		gbc_lblResourcesCopied.gridy = 5;
		leftPanel.add(lblResourcesCopied, gbc_lblResourcesCopied);
		
		JLabel lblResourcesCopiedCount = new JLabel("0");
		GridBagConstraints gbc_lblResourcesCopiedCount = new GridBagConstraints();
		gbc_lblResourcesCopiedCount.anchor = GridBagConstraints.WEST;
		gbc_lblResourcesCopiedCount.insets = new Insets(0, 0, 5, 5);
		gbc_lblResourcesCopiedCount.gridx = 3;
		gbc_lblResourcesCopiedCount.gridy = 5;
		leftPanel.add(lblResourcesCopiedCount, gbc_lblResourcesCopiedCount);
		
		JLabel lblResourcesCopiedPercent = new JLabel("0%");
		GridBagConstraints gbc_lblResourcesCopiedPercent = new GridBagConstraints();
		gbc_lblResourcesCopiedPercent.insets = new Insets(0, 0, 5, 5);
		gbc_lblResourcesCopiedPercent.gridx = 4;
		gbc_lblResourcesCopiedPercent.gridy = 5;
		leftPanel.add(lblResourcesCopiedPercent, gbc_lblResourcesCopiedPercent);
		
		JLabel lblArchivesSkipped = new JLabel("Archives Skipped:");
		lblArchivesSkipped.setHorizontalAlignment(SwingConstants.RIGHT);
		GridBagConstraints gbc_lblArchivesSkipped = new GridBagConstraints();
		gbc_lblArchivesSkipped.anchor = GridBagConstraints.EAST;
		gbc_lblArchivesSkipped.insets = new Insets(0, 0, 5, 5);
		gbc_lblArchivesSkipped.gridx = 2;
		gbc_lblArchivesSkipped.gridy = 7;
		leftPanel.add(lblArchivesSkipped, gbc_lblArchivesSkipped);
		
		JLabel lblArchivesSkippedCount = new JLabel("0");
		GridBagConstraints gbc_lblArchivesSkippedCount = new GridBagConstraints();
		gbc_lblArchivesSkippedCount.anchor = GridBagConstraints.WEST;
		gbc_lblArchivesSkippedCount.insets = new Insets(0, 0, 5, 5);
		gbc_lblArchivesSkippedCount.gridx = 3;
		gbc_lblArchivesSkippedCount.gridy = 7;
		leftPanel.add(lblArchivesSkippedCount, gbc_lblArchivesSkippedCount);
		
		JLabel lblArchivesSkippedPercent = new JLabel("0%");
		GridBagConstraints gbc_lblArchivesSkippedPercent = new GridBagConstraints();
		gbc_lblArchivesSkippedPercent.insets = new Insets(0, 0, 5, 5);
		gbc_lblArchivesSkippedPercent.gridx = 4;
		gbc_lblArchivesSkippedPercent.gridy = 7;
		leftPanel.add(lblArchivesSkippedPercent, gbc_lblArchivesSkippedPercent);
		
		JLabel lblResourcesSkipped = new JLabel("Resources Skipped:");
		GridBagConstraints gbc_lblResourcesSkipped = new GridBagConstraints();
		gbc_lblResourcesSkipped.anchor = GridBagConstraints.EAST;
		gbc_lblResourcesSkipped.insets = new Insets(0, 0, 5, 5);
		gbc_lblResourcesSkipped.gridx = 2;
		gbc_lblResourcesSkipped.gridy = 8;
		leftPanel.add(lblResourcesSkipped, gbc_lblResourcesSkipped);
		
		JLabel lblResourcesSkippedCount = new JLabel("0");
		GridBagConstraints gbc_lblResourcesSkippedCount = new GridBagConstraints();
		gbc_lblResourcesSkippedCount.insets = new Insets(0, 0, 5, 5);
		gbc_lblResourcesSkippedCount.anchor = GridBagConstraints.WEST;
		gbc_lblResourcesSkippedCount.gridx = 3;
		gbc_lblResourcesSkippedCount.gridy = 8;
		leftPanel.add(lblResourcesSkippedCount, gbc_lblResourcesSkippedCount);
		
		JLabel labelResourcesSkippedPercent = new JLabel("0%");
		GridBagConstraints gbc_labelResourcesSkippedPercent = new GridBagConstraints();
		gbc_labelResourcesSkippedPercent.insets = new Insets(0, 0, 5, 5);
		gbc_labelResourcesSkippedPercent.gridx = 4;
		gbc_labelResourcesSkippedPercent.gridy = 8;
		leftPanel.add(labelResourcesSkippedPercent, gbc_labelResourcesSkippedPercent);
		
		JLabel lblArchivesFailed = new JLabel("Archives Failed:");
		lblArchivesFailed.setHorizontalAlignment(SwingConstants.RIGHT);
		GridBagConstraints gbc_lblArchivesFailed = new GridBagConstraints();
		gbc_lblArchivesFailed.anchor = GridBagConstraints.EAST;
		gbc_lblArchivesFailed.insets = new Insets(0, 0, 5, 5);
		gbc_lblArchivesFailed.gridx = 2;
		gbc_lblArchivesFailed.gridy = 10;
		leftPanel.add(lblArchivesFailed, gbc_lblArchivesFailed);
		
		JLabel lblArchivesFailedCount = new JLabel("0");
		GridBagConstraints gbc_lblArchivesFailedCount = new GridBagConstraints();
		gbc_lblArchivesFailedCount.anchor = GridBagConstraints.WEST;
		gbc_lblArchivesFailedCount.insets = new Insets(0, 0, 5, 5);
		gbc_lblArchivesFailedCount.gridx = 3;
		gbc_lblArchivesFailedCount.gridy = 10;
		leftPanel.add(lblArchivesFailedCount, gbc_lblArchivesFailedCount);
		
		JLabel lblArchivesFailedPercent = new JLabel("0%");
		GridBagConstraints gbc_lblArchivesFailedPercent = new GridBagConstraints();
		gbc_lblArchivesFailedPercent.insets = new Insets(0, 0, 5, 5);
		gbc_lblArchivesFailedPercent.gridx = 4;
		gbc_lblArchivesFailedPercent.gridy = 10;
		leftPanel.add(lblArchivesFailedPercent, gbc_lblArchivesFailedPercent);
		
		JLabel lblResourcesFailed = new JLabel("Resources Failed:");
		GridBagConstraints gbc_lblResourcesFailed = new GridBagConstraints();
		gbc_lblResourcesFailed.anchor = GridBagConstraints.EAST;
		gbc_lblResourcesFailed.insets = new Insets(0, 0, 0, 5);
		gbc_lblResourcesFailed.gridx = 2;
		gbc_lblResourcesFailed.gridy = 11;
		leftPanel.add(lblResourcesFailed, gbc_lblResourcesFailed);
		
		JLabel lblResourcesFailedCount = new JLabel("0");
		GridBagConstraints gbc_lblResourcesFailedCount = new GridBagConstraints();
		gbc_lblResourcesFailedCount.anchor = GridBagConstraints.WEST;
		gbc_lblResourcesFailedCount.insets = new Insets(0, 0, 0, 5);
		gbc_lblResourcesFailedCount.gridx = 3;
		gbc_lblResourcesFailedCount.gridy = 11;
		leftPanel.add(lblResourcesFailedCount, gbc_lblResourcesFailedCount);
		
		JLabel lblResourcesFailedPercent = new JLabel("0%");
		GridBagConstraints gbc_lblResourcesFailedPercent = new GridBagConstraints();
		gbc_lblResourcesFailedPercent.insets = new Insets(0, 0, 0, 5);
		gbc_lblResourcesFailedPercent.gridx = 4;
		gbc_lblResourcesFailedPercent.gridy = 11;
		leftPanel.add(lblResourcesFailedPercent, gbc_lblResourcesFailedPercent);
		
		copyOperations.addBatchListener(() -> {
			SwingUtilities.invokeLater(() -> {
				
				//Set all of the label text
				
			});
		});
	}
	
	private void setupRightProgressPane(JPanel progressPanel) {
		JPanel rightPanel = new JPanel();
		progressPanel.add(rightPanel);
		GridBagLayout gbl_rightPanel = new GridBagLayout();
		gbl_rightPanel.columnWidths = new int[] {30, 0, 90, 0, 30, 30, 0, 0};
		gbl_rightPanel.rowHeights = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
		gbl_rightPanel.columnWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		gbl_rightPanel.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		rightPanel.setLayout(gbl_rightPanel);
		
		JLabel lblNewLabel = new JLabel("Process Operation");
		GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
		gbc_lblNewLabel.gridwidth = 7;
		gbc_lblNewLabel.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel.gridx = 0;
		gbc_lblNewLabel.gridy = 0;
		rightPanel.add(lblNewLabel, gbc_lblNewLabel);
		
		JLabel lblTotalArchives = new JLabel("Total Archives:");
		GridBagConstraints gbc_lblTotalArchives = new GridBagConstraints();
		gbc_lblTotalArchives.anchor = GridBagConstraints.EAST;
		gbc_lblTotalArchives.insets = new Insets(0, 0, 5, 5);
		gbc_lblTotalArchives.gridx = 1;
		gbc_lblTotalArchives.gridy = 1;
		rightPanel.add(lblTotalArchives, gbc_lblTotalArchives);
		
		JLabel lblTotalArchvesCount = new JLabel("0");
		GridBagConstraints gbc_lblTotalArchvesCount = new GridBagConstraints();
		gbc_lblTotalArchvesCount.anchor = GridBagConstraints.WEST;
		gbc_lblTotalArchvesCount.insets = new Insets(0, 0, 5, 5);
		gbc_lblTotalArchvesCount.gridx = 2;
		gbc_lblTotalArchvesCount.gridy = 1;
		rightPanel.add(lblTotalArchvesCount, gbc_lblTotalArchvesCount);
		
		JLabel lblTotalResources = new JLabel("Total Resources:");
		GridBagConstraints gbc_lblTotalResources = new GridBagConstraints();
		gbc_lblTotalResources.anchor = GridBagConstraints.EAST;
		gbc_lblTotalResources.insets = new Insets(0, 0, 5, 5);
		gbc_lblTotalResources.gridx = 1;
		gbc_lblTotalResources.gridy = 2;
		rightPanel.add(lblTotalResources, gbc_lblTotalResources);
		
		JLabel lblTotalResourcesCount = new JLabel("0");
		GridBagConstraints gbc_lblTotalResourcesCount = new GridBagConstraints();
		gbc_lblTotalResourcesCount.anchor = GridBagConstraints.WEST;
		gbc_lblTotalResourcesCount.insets = new Insets(0, 0, 5, 5);
		gbc_lblTotalResourcesCount.gridx = 2;
		gbc_lblTotalResourcesCount.gridy = 2;
		rightPanel.add(lblTotalResourcesCount, gbc_lblTotalResourcesCount);
		
		JLabel lblArchivesProcessed = new JLabel("Archives Processed:");
		GridBagConstraints gbc_lblArchivesProcessed = new GridBagConstraints();
		gbc_lblArchivesProcessed.anchor = GridBagConstraints.EAST;
		gbc_lblArchivesProcessed.insets = new Insets(0, 0, 5, 5);
		gbc_lblArchivesProcessed.gridx = 1;
		gbc_lblArchivesProcessed.gridy = 4;
		rightPanel.add(lblArchivesProcessed, gbc_lblArchivesProcessed);
		lblArchivesProcessed.setHorizontalAlignment(SwingConstants.RIGHT);
		
		JLabel lblArchivesProcessedCount = new JLabel("0");
		GridBagConstraints gbc_lblArchivesProcessedCount = new GridBagConstraints();
		gbc_lblArchivesProcessedCount.anchor = GridBagConstraints.WEST;
		gbc_lblArchivesProcessedCount.insets = new Insets(0, 0, 5, 5);
		gbc_lblArchivesProcessedCount.gridx = 2;
		gbc_lblArchivesProcessedCount.gridy = 4;
		rightPanel.add(lblArchivesProcessedCount, gbc_lblArchivesProcessedCount);
		
		JLabel lblArchivesProcessedPercent = new JLabel("0%");
		GridBagConstraints gbc_lblArchivesProcessedPercent = new GridBagConstraints();
		gbc_lblArchivesProcessedPercent.insets = new Insets(0, 0, 5, 5);
		gbc_lblArchivesProcessedPercent.gridx = 3;
		gbc_lblArchivesProcessedPercent.gridy = 4;
		rightPanel.add(lblArchivesProcessedPercent, gbc_lblArchivesProcessedPercent);
		
		JLabel lblResourcesProcessed = new JLabel("Resources Processed:");
		GridBagConstraints gbc_lblResourcesProcessed = new GridBagConstraints();
		gbc_lblResourcesProcessed.anchor = GridBagConstraints.EAST;
		gbc_lblResourcesProcessed.insets = new Insets(0, 0, 5, 5);
		gbc_lblResourcesProcessed.gridx = 1;
		gbc_lblResourcesProcessed.gridy = 5;
		rightPanel.add(lblResourcesProcessed, gbc_lblResourcesProcessed);
		
		JLabel lblResourcesProcessedCount = new JLabel("0");
		GridBagConstraints gbc_lblResourcesProcessedCount = new GridBagConstraints();
		gbc_lblResourcesProcessedCount.anchor = GridBagConstraints.WEST;
		gbc_lblResourcesProcessedCount.insets = new Insets(0, 0, 5, 5);
		gbc_lblResourcesProcessedCount.gridx = 2;
		gbc_lblResourcesProcessedCount.gridy = 5;
		rightPanel.add(lblResourcesProcessedCount, gbc_lblResourcesProcessedCount);
		
		JLabel lblResourcesProcessedPercent = new JLabel("0%");
		GridBagConstraints gbc_lblResourcesProcessedPercent = new GridBagConstraints();
		gbc_lblResourcesProcessedPercent.insets = new Insets(0, 0, 5, 5);
		gbc_lblResourcesProcessedPercent.gridx = 3;
		gbc_lblResourcesProcessedPercent.gridy = 5;
		rightPanel.add(lblResourcesProcessedPercent, gbc_lblResourcesProcessedPercent);
		
		JLabel lblArchivesSkipped = new JLabel("Archives Skipped:");
		GridBagConstraints gbc_lblArchivesSkipped = new GridBagConstraints();
		gbc_lblArchivesSkipped.anchor = GridBagConstraints.EAST;
		gbc_lblArchivesSkipped.insets = new Insets(0, 0, 5, 5);
		gbc_lblArchivesSkipped.gridx = 1;
		gbc_lblArchivesSkipped.gridy = 7;
		rightPanel.add(lblArchivesSkipped, gbc_lblArchivesSkipped);
		
		JLabel lblArchivesSkippedCount = new JLabel("0");
		GridBagConstraints gbc_lblArchivesSkippedCount = new GridBagConstraints();
		gbc_lblArchivesSkippedCount.anchor = GridBagConstraints.WEST;
		gbc_lblArchivesSkippedCount.insets = new Insets(0, 0, 5, 5);
		gbc_lblArchivesSkippedCount.gridx = 2;
		gbc_lblArchivesSkippedCount.gridy = 7;
		rightPanel.add(lblArchivesSkippedCount, gbc_lblArchivesSkippedCount);
		
		JLabel lblArchivesSkippedPercent = new JLabel("0%");
		GridBagConstraints gbc_lblArchivesSkippedPercent = new GridBagConstraints();
		gbc_lblArchivesSkippedPercent.insets = new Insets(0, 0, 5, 5);
		gbc_lblArchivesSkippedPercent.gridx = 3;
		gbc_lblArchivesSkippedPercent.gridy = 7;
		rightPanel.add(lblArchivesSkippedPercent, gbc_lblArchivesSkippedPercent);
		
		JLabel lblResourcesSkipped = new JLabel("Resources Skipped:");
		GridBagConstraints gbc_lblResourcesSkipped = new GridBagConstraints();
		gbc_lblResourcesSkipped.anchor = GridBagConstraints.EAST;
		gbc_lblResourcesSkipped.insets = new Insets(0, 0, 5, 5);
		gbc_lblResourcesSkipped.gridx = 1;
		gbc_lblResourcesSkipped.gridy = 8;
		rightPanel.add(lblResourcesSkipped, gbc_lblResourcesSkipped);
		
		JLabel lblResourcesSkippedCount = new JLabel("0");
		GridBagConstraints gbc_lblResourcesSkippedCount = new GridBagConstraints();
		gbc_lblResourcesSkippedCount.anchor = GridBagConstraints.WEST;
		gbc_lblResourcesSkippedCount.insets = new Insets(0, 0, 5, 5);
		gbc_lblResourcesSkippedCount.gridx = 2;
		gbc_lblResourcesSkippedCount.gridy = 8;
		rightPanel.add(lblResourcesSkippedCount, gbc_lblResourcesSkippedCount);
		
		JLabel lblResourcesSkippedPercent = new JLabel("0%");
		GridBagConstraints gbc_lblResourcesSkippedPercent = new GridBagConstraints();
		gbc_lblResourcesSkippedPercent.insets = new Insets(0, 0, 5, 5);
		gbc_lblResourcesSkippedPercent.gridx = 3;
		gbc_lblResourcesSkippedPercent.gridy = 8;
		rightPanel.add(lblResourcesSkippedPercent, gbc_lblResourcesSkippedPercent);
		
		JLabel lblArchivesFailed = new JLabel("Archives Failed:");
		GridBagConstraints gbc_lblArchivesFailed = new GridBagConstraints();
		gbc_lblArchivesFailed.anchor = GridBagConstraints.EAST;
		gbc_lblArchivesFailed.insets = new Insets(0, 0, 5, 5);
		gbc_lblArchivesFailed.gridx = 1;
		gbc_lblArchivesFailed.gridy = 10;
		rightPanel.add(lblArchivesFailed, gbc_lblArchivesFailed);
		
		JLabel lblArchivesFailedCount = new JLabel("0");
		GridBagConstraints gbc_lblArchivesFailedCount = new GridBagConstraints();
		gbc_lblArchivesFailedCount.anchor = GridBagConstraints.WEST;
		gbc_lblArchivesFailedCount.insets = new Insets(0, 0, 5, 5);
		gbc_lblArchivesFailedCount.gridx = 2;
		gbc_lblArchivesFailedCount.gridy = 10;
		rightPanel.add(lblArchivesFailedCount, gbc_lblArchivesFailedCount);
		
		JLabel lblArchivesFailedPercent = new JLabel("0%");
		GridBagConstraints gbc_lblArchivesFailedPercent = new GridBagConstraints();
		gbc_lblArchivesFailedPercent.insets = new Insets(0, 0, 5, 5);
		gbc_lblArchivesFailedPercent.gridx = 3;
		gbc_lblArchivesFailedPercent.gridy = 10;
		rightPanel.add(lblArchivesFailedPercent, gbc_lblArchivesFailedPercent);
		
		JLabel lblResourcesFailed = new JLabel("Resources Failed:");
		GridBagConstraints gbc_lblResourcesFailed = new GridBagConstraints();
		gbc_lblResourcesFailed.anchor = GridBagConstraints.EAST;
		gbc_lblResourcesFailed.insets = new Insets(0, 0, 0, 5);
		gbc_lblResourcesFailed.gridx = 1;
		gbc_lblResourcesFailed.gridy = 11;
		rightPanel.add(lblResourcesFailed, gbc_lblResourcesFailed);
		
		JLabel lblResourcesFailedCount = new JLabel("0");
		GridBagConstraints gbc_lblResourcesFailedCount = new GridBagConstraints();
		gbc_lblResourcesFailedCount.anchor = GridBagConstraints.WEST;
		gbc_lblResourcesFailedCount.insets = new Insets(0, 0, 0, 5);
		gbc_lblResourcesFailedCount.gridx = 2;
		gbc_lblResourcesFailedCount.gridy = 11;
		rightPanel.add(lblResourcesFailedCount, gbc_lblResourcesFailedCount);
		
		JLabel lblResourcesFailedPercent = new JLabel("0%");
		GridBagConstraints gbc_lblResourcesFailedPercent = new GridBagConstraints();
		gbc_lblResourcesFailedPercent.insets = new Insets(0, 0, 0, 5);
		gbc_lblResourcesFailedPercent.gridx = 3;
		gbc_lblResourcesFailedPercent.gridy = 11;
		rightPanel.add(lblResourcesFailedPercent, gbc_lblResourcesFailedPercent);
	}
	
	private BatchRunner genCopyBatches(File dir) {
		BatchRunner batchRunner = new BatchRunner("Copy Operations");
		if(dir != null) {
			for(File f : dir.listFiles()) {
				Batch b = new Batch(f.getName());
				batchRunner.addBatch(b);
			}
		}
		return batchRunner;
	}
	
	private void setGridBatches(BatchContainer batch, FixedCellGrid grid) {
		Collection<BatchedCallable> batches = batch.getRunnables();
		for(int i = 0; i < batches.size(); i++) {
			tabbedPane.addTab("File " + i, new BatchOperationComponent(batch));
		}
	}
	
	private BatchRunner getProcessBatches() {
		throw new UnsupportedOperationException("Not yet implemented");
	}

	@Override
	public void update() {
		frame.repaint();
	}
	
	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		System.out.println("Scrolled: " + e.getComponent());
		System.out.println("Child: " + e.getComponent().getComponentAt(e.getPoint()));
		if(e.getSource() instanceof JTabbedPane) {
			JTabbedPane pane = (JTabbedPane) e.getSource();
			Component scrolledComponent = pane.getComponentAt(e.getPoint());
			if(!(scrolledComponent instanceof FixedCellSizeGrid) && scrolledComponent.getParent() instanceof JTabbedPane) {
				int units = e.getWheelRotation();
				System.out.println(units);
				int oldIndex = pane.getSelectedIndex();
				int newIndex = oldIndex + units;
				if(newIndex < 0) {
					pane.setSelectedIndex(0);
				}
				else if (newIndex >= pane.getTabCount()) {
					pane.setSelectedIndex(pane.getTabCount() - 1);
				}
				else {
					pane.setSelectedIndex(newIndex);
				}
			}
			else if(scrolledComponent instanceof FixedCellSizeGrid) {
				FixedCellSizeGrid grid = (FixedCellSizeGrid) scrolledComponent;
				grid.scroll(e.getWheelRotation());
			}
		}
		else {
			//System.out.println(e.getSource());
		}
	}
	
	private FixedCellGrid genGridPanel() {
		FixedCellGrid gridPanel = new FixedCellSizeGrid(new Dimension(385, 385), new Dimension(100, 100), 0);
		gridPanel.setVisible(true);
		
		return gridPanel;
	}
	
	private int getWantedThreads() {
		int ret = threadSlider.getValue();
		if(ret < 1) {
			return 1;
		}
		return ret;
	}
}
