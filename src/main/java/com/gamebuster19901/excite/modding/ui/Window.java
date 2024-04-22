package com.gamebuster19901.excite.modding.ui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Insets;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.io.PrintStream;
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
import javax.swing.JLabel;
import javax.swing.JSeparator;
import javax.swing.JTabbedPane;
import javax.swing.JSlider;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
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
	private JTextField textField;
	private JTextField textField_1;
	private JSlider threadSlider;
	private JLabel lblThreads;
	private static Window window;
	private final JProgressBar progressBar = new JProgressBar();
	private final JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
	private final FixedCellGrid gridPanel = genGridPanel();
	private JTable table;
	
	private BatchRunner copyOperations;
	private BatchRunner processOperations;

	/**
	 * Launch the application.
	 * @throws InterruptedException 
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
	 * Initialize the contents of the frame.
	 * @throws InterruptedException 
	 */
	private void initialize() throws InterruptedException {
		
		copyOperations = genCopyBatches();
		
		setupFrame();
		setupTabbedPane();
	}
	
	private void setupFrame() {
		frame = new JFrame();
		frame.setTitle("ExciteModder");
		frame.setBounds(100, 100, 1000, 680);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		//frame.setResizable(false);
		frame.setVisible(true);
		
		JLabel lblUnmoddedDirectory = new JLabel("Source Directory:");
		lblUnmoddedDirectory.setToolTipText("The directory of the unmodified, original ripped files.");
		lblUnmoddedDirectory.setBounds(12, 12, 165, 15);
		frame.getContentPane().add(lblUnmoddedDirectory);
		
		JLabel lblModdedDirectory = new JLabel("Destination Directory:");
		lblModdedDirectory.setToolTipText("Where ExciteModder will copy modified game files to.");
		lblModdedDirectory.setBounds(12, 39, 165, 15);
		frame.getContentPane().add(lblModdedDirectory);
		
		textField = new JTextField();
		textField.setBounds(171, 37, 578, 19);
		frame.getContentPane().add(textField);
		textField.setColumns(10);
		
		textField_1 = new JTextField();
		textField_1.setColumns(10);
		textField_1.setBounds(171, 10, 578, 19);
		frame.getContentPane().add(textField_1);
		
		JButton btnChangeSource = new JButton("Change");
		btnChangeSource.setToolTipText("Change where ExciteModder will copy the game files from");
		btnChangeSource.setBounds(761, 9, 117, 19);
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
		tabbedPane.addMouseWheelListener(this);
	}
	
	private void setupTabbedPane() {
		tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
		
		tabbedPane.setBounds(0, 129, 1000, 511);
		frame.getContentPane().add(tabbedPane);
		
		setupConsoleOutputTab();
		setupStatusTab();
		setupProgressTab();
		
		for(Batcher b : copyOperations.getBatches()) {
			tabbedPane.addTab(b.getName(), null);
		}
		
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
		
		JScrollPane scrollPane = new JScrollPane(textArea);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		tabbedPane.addTab("Console Output", null, scrollPane, null);
	}
	
	public void setupStatusTab() {
		tabbedPane.addTab("Status", null, gridPanel, null);
		Iterator<Batcher> batches = copyOperations.getBatches().iterator();
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
		JPanel panel_1 = new JPanel();
		progressPanel.add(panel_1);
		GridBagLayout gbl_panel_1 = new GridBagLayout();
		gbl_panel_1.columnWidths = new int[] {100, 0, 70, 90, 0, 0, 0};
		gbl_panel_1.rowHeights = new int[] {0, 15, 0, 30, 0, 0, 30, 30, 0, 0, 30, 0};
		gbl_panel_1.columnWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		gbl_panel_1.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		panel_1.setLayout(gbl_panel_1);
		
		JLabel lblCopyOperation = new JLabel("Copy Operation");
		GridBagConstraints gbc_lblCopyOperation = new GridBagConstraints();
		gbc_lblCopyOperation.gridwidth = 6;
		gbc_lblCopyOperation.insets = new Insets(0, 0, 5, 0);
		gbc_lblCopyOperation.gridx = 0;
		gbc_lblCopyOperation.gridy = 0;
		panel_1.add(lblCopyOperation, gbc_lblCopyOperation);
		BatchOperationComponent allBatchesCopy = new BatchOperationComponent(copyOperations);
		allBatchesCopy.setToolTipText("All Batches");
		GridBagConstraints gbc_allBatches = new GridBagConstraints();
		gbc_allBatches.fill = GridBagConstraints.BOTH;
		gbc_allBatches.gridheight = 9;
		gbc_allBatches.insets = new Insets(0, 0, 0, 5);
		gbc_allBatches.gridx = 0;
		gbc_allBatches.gridy = 1;
		panel_1.add(allBatchesCopy, gbc_allBatches);
		
		JSeparator separator_1 = new JSeparator();
		GridBagConstraints gbc_separator_1 = new GridBagConstraints();
		gbc_separator_1.fill = GridBagConstraints.BOTH;
		gbc_separator_1.gridheight = 10;
		gbc_separator_1.insets = new Insets(0, 0, 5, 5);
		gbc_separator_1.gridx = 1;
		gbc_separator_1.gridy = 0;
		panel_1.add(separator_1, gbc_separator_1);
		
		JLabel lblTotalArchives = new JLabel("Total Archives:");
		lblTotalArchives.setHorizontalAlignment(SwingConstants.CENTER);
		GridBagConstraints gbc_lblTotalArchives = new GridBagConstraints();
		gbc_lblTotalArchives.insets = new Insets(0, 0, 5, 5);
		gbc_lblTotalArchives.anchor = GridBagConstraints.NORTHEAST;
		gbc_lblTotalArchives.gridx = 2;
		gbc_lblTotalArchives.gridy = 1;
		panel_1.add(lblTotalArchives, gbc_lblTotalArchives);
		
		JLabel lblTotalArchivesCount = new JLabel("0");
		GridBagConstraints gbc_lblTotalArchivesCount = new GridBagConstraints();
		gbc_lblTotalArchivesCount.anchor = GridBagConstraints.WEST;
		gbc_lblTotalArchivesCount.insets = new Insets(0, 0, 5, 5);
		gbc_lblTotalArchivesCount.gridx = 3;
		gbc_lblTotalArchivesCount.gridy = 1;
		panel_1.add(lblTotalArchivesCount, gbc_lblTotalArchivesCount);
		
		JLabel label_5 = new JLabel("100%");
		GridBagConstraints gbc_label_5 = new GridBagConstraints();
		gbc_label_5.insets = new Insets(0, 0, 5, 0);
		gbc_label_5.gridx = 4;
		gbc_label_5.gridy = 1;
		panel_1.add(label_5, gbc_label_5);
		
		JLabel lblTotalResources = new JLabel("Total Resources:");
		lblTotalResources.setHorizontalAlignment(SwingConstants.RIGHT);
		GridBagConstraints gbc_lblTotalResources = new GridBagConstraints();
		gbc_lblTotalResources.anchor = GridBagConstraints.EAST;
		gbc_lblTotalResources.insets = new Insets(0, 0, 5, 5);
		gbc_lblTotalResources.gridx = 2;
		gbc_lblTotalResources.gridy = 2;
		panel_1.add(lblTotalResources, gbc_lblTotalResources);
		
		JLabel lblTotalResourcesCount = new JLabel("0");
		GridBagConstraints gbc_lblTotalResourcesCount = new GridBagConstraints();
		gbc_lblTotalResourcesCount.anchor = GridBagConstraints.WEST;
		gbc_lblTotalResourcesCount.insets = new Insets(0, 0, 5, 5);
		gbc_lblTotalResourcesCount.gridx = 3;
		gbc_lblTotalResourcesCount.gridy = 2;
		panel_1.add(lblTotalResourcesCount, gbc_lblTotalResourcesCount);
		
		JLabel label_6 = new JLabel("100%");
		GridBagConstraints gbc_label_6 = new GridBagConstraints();
		gbc_label_6.insets = new Insets(0, 0, 5, 0);
		gbc_label_6.gridx = 4;
		gbc_label_6.gridy = 2;
		panel_1.add(label_6, gbc_label_6);
		
		JLabel lblArchivesCopied = new JLabel("Archives Copied:");
		GridBagConstraints gbc_lblArchivesCopied = new GridBagConstraints();
		gbc_lblArchivesCopied.anchor = GridBagConstraints.EAST;
		gbc_lblArchivesCopied.insets = new Insets(0, 0, 5, 5);
		gbc_lblArchivesCopied.gridx = 2;
		gbc_lblArchivesCopied.gridy = 4;
		panel_1.add(lblArchivesCopied, gbc_lblArchivesCopied);
		
		JLabel label_1 = new JLabel("99");
		GridBagConstraints gbc_label_1 = new GridBagConstraints();
		gbc_label_1.anchor = GridBagConstraints.WEST;
		gbc_label_1.insets = new Insets(0, 0, 5, 5);
		gbc_label_1.gridx = 3;
		gbc_label_1.gridy = 4;
		panel_1.add(label_1, gbc_label_1);
		
		JLabel label_7 = new JLabel("0%");
		GridBagConstraints gbc_label_7 = new GridBagConstraints();
		gbc_label_7.insets = new Insets(0, 0, 5, 0);
		gbc_label_7.gridx = 4;
		gbc_label_7.gridy = 4;
		panel_1.add(label_7, gbc_label_7);
		
		JLabel lblResourcesCopied = new JLabel("Resources Copied:");
		GridBagConstraints gbc_lblResourcesCopied = new GridBagConstraints();
		gbc_lblResourcesCopied.anchor = GridBagConstraints.EAST;
		gbc_lblResourcesCopied.insets = new Insets(0, 0, 5, 5);
		gbc_lblResourcesCopied.gridx = 2;
		gbc_lblResourcesCopied.gridy = 5;
		panel_1.add(lblResourcesCopied, gbc_lblResourcesCopied);
		
		JLabel label_2 = new JLabel("0");
		GridBagConstraints gbc_label_2 = new GridBagConstraints();
		gbc_label_2.anchor = GridBagConstraints.WEST;
		gbc_label_2.insets = new Insets(0, 0, 5, 5);
		gbc_label_2.gridx = 3;
		gbc_label_2.gridy = 5;
		panel_1.add(label_2, gbc_label_2);
		
		JLabel label_8 = new JLabel("0%");
		GridBagConstraints gbc_label_8 = new GridBagConstraints();
		gbc_label_8.insets = new Insets(0, 0, 5, 0);
		gbc_label_8.gridx = 4;
		gbc_label_8.gridy = 5;
		panel_1.add(label_8, gbc_label_8);
		
		JLabel lblArchivesProcessed_1 = new JLabel("Archives Skipped:");
		lblArchivesProcessed_1.setHorizontalAlignment(SwingConstants.RIGHT);
		GridBagConstraints gbc_lblArchivesProcessed_1 = new GridBagConstraints();
		gbc_lblArchivesProcessed_1.anchor = GridBagConstraints.EAST;
		gbc_lblArchivesProcessed_1.insets = new Insets(0, 0, 5, 5);
		gbc_lblArchivesProcessed_1.gridx = 2;
		gbc_lblArchivesProcessed_1.gridy = 8;
		panel_1.add(lblArchivesProcessed_1, gbc_lblArchivesProcessed_1);
		
		JLabel label_3 = new JLabel("0");
		GridBagConstraints gbc_label_3 = new GridBagConstraints();
		gbc_label_3.anchor = GridBagConstraints.WEST;
		gbc_label_3.insets = new Insets(0, 0, 5, 5);
		gbc_label_3.gridx = 3;
		gbc_label_3.gridy = 8;
		panel_1.add(label_3, gbc_label_3);
		
		JLabel label_10 = new JLabel("0%");
		GridBagConstraints gbc_label_10 = new GridBagConstraints();
		gbc_label_10.insets = new Insets(0, 0, 5, 0);
		gbc_label_10.gridx = 4;
		gbc_label_10.gridy = 8;
		panel_1.add(label_10, gbc_label_10);
		
		JLabel lblResourcesSkipped = new JLabel("Resources Skipped:");
		GridBagConstraints gbc_lblResourcesSkipped = new GridBagConstraints();
		gbc_lblResourcesSkipped.anchor = GridBagConstraints.EAST;
		gbc_lblResourcesSkipped.insets = new Insets(0, 0, 5, 5);
		gbc_lblResourcesSkipped.gridx = 2;
		gbc_lblResourcesSkipped.gridy = 9;
		panel_1.add(lblResourcesSkipped, gbc_lblResourcesSkipped);
		
		JLabel label_4 = new JLabel("0");
		GridBagConstraints gbc_label_4 = new GridBagConstraints();
		gbc_label_4.insets = new Insets(0, 0, 5, 5);
		gbc_label_4.anchor = GridBagConstraints.WEST;
		gbc_label_4.gridx = 3;
		gbc_label_4.gridy = 9;
		panel_1.add(label_4, gbc_label_4);
	}
	
	private void setupRightProgressPane(JPanel progressPanel) {
		JPanel panel_2 = new JPanel();
		progressPanel.add(panel_2);
		GridBagLayout gbl_panel_2 = new GridBagLayout();
		gbl_panel_2.columnWidths = new int[] {30, 0, 0, 0, 30, 30, 0, 0};
		gbl_panel_2.rowHeights = new int[]{0, 0, 0, 0, 0, 0, 0};
		gbl_panel_2.columnWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		gbl_panel_2.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		panel_2.setLayout(gbl_panel_2);
		
		JLabel lblNewLabel = new JLabel("Process Operation");
		GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
		gbc_lblNewLabel.gridwidth = 7;
		gbc_lblNewLabel.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel.gridx = 0;
		gbc_lblNewLabel.gridy = 0;
		panel_2.add(lblNewLabel, gbc_lblNewLabel);
		
		JLabel lblArchivesProcessed = new JLabel("Archives Processed:");
		GridBagConstraints gbc_lblArchivesProcessed = new GridBagConstraints();
		gbc_lblArchivesProcessed.insets = new Insets(0, 0, 5, 5);
		gbc_lblArchivesProcessed.gridx = 1;
		gbc_lblArchivesProcessed.gridy = 3;
		panel_2.add(lblArchivesProcessed, gbc_lblArchivesProcessed);
		lblArchivesProcessed.setHorizontalAlignment(SwingConstants.RIGHT);
		
		JLabel lblArchivesprocessed = new JLabel("0");
		GridBagConstraints gbc_lblArchivesprocessed = new GridBagConstraints();
		gbc_lblArchivesprocessed.insets = new Insets(0, 0, 5, 5);
		gbc_lblArchivesprocessed.gridx = 2;
		gbc_lblArchivesprocessed.gridy = 3;
		panel_2.add(lblArchivesprocessed, gbc_lblArchivesprocessed);
		
		JLabel label_9 = new JLabel("0%");
		GridBagConstraints gbc_label_9 = new GridBagConstraints();
		gbc_label_9.insets = new Insets(0, 0, 5, 5);
		gbc_label_9.gridx = 3;
		gbc_label_9.gridy = 3;
		panel_2.add(label_9, gbc_label_9);
		
		JLabel lblResourcesProcessed = new JLabel("Resources Processed:");
		GridBagConstraints gbc_lblResourcesProcessed = new GridBagConstraints();
		gbc_lblResourcesProcessed.insets = new Insets(0, 0, 5, 5);
		gbc_lblResourcesProcessed.gridx = 1;
		gbc_lblResourcesProcessed.gridy = 4;
		panel_2.add(lblResourcesProcessed, gbc_lblResourcesProcessed);
		
		JLabel label = new JLabel("100");
		GridBagConstraints gbc_label = new GridBagConstraints();
		gbc_label.insets = new Insets(0, 0, 5, 5);
		gbc_label.gridx = 2;
		gbc_label.gridy = 4;
		panel_2.add(label, gbc_label);
		
		JLabel label_11 = new JLabel("0%");
		GridBagConstraints gbc_label_11 = new GridBagConstraints();
		gbc_label_11.insets = new Insets(0, 0, 5, 5);
		gbc_label_11.gridx = 3;
		gbc_label_11.gridy = 4;
		panel_2.add(label_11, gbc_label_11);
	}
	
	private BatchRunner genCopyBatches() {
		BatchRunner batchRunner = new BatchRunner("Copy Operations");
		for(int i = 0; i < 10; i++) {
			Batch b = new Batch("File " + i);
			batchRunner.addBatch(b);
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
