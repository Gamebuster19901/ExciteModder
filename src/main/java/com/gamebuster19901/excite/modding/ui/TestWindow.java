package com.gamebuster19901.excite.modding.ui;

import java.awt.Dimension;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;

import com.gamebuster19901.excite.modding.concurrent.Batch;
import com.gamebuster19901.excite.modding.concurrent.BatchRunner;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import javax.swing.JTabbedPane;
import java.awt.Insets;
import java.util.Random;

public class TestWindow {

	private JFrame frame;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					TestWindow window = new TestWindow();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public TestWindow() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		System.out.println(Thread.currentThread());
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		Batch batch = new Batch("Test");
		BatchRunner r = new BatchRunner("TestRunner");
		r.addBatch(batch);

		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{450, 0};
		gridBagLayout.rowHeights = new int[]{263, 0};
		gridBagLayout.columnWeights = new double[]{1.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{1.0, Double.MIN_VALUE};
		frame.getContentPane().setLayout(gridBagLayout);
		JPanel panel = new JPanel();
		GridBagLayout gbl_panel = new GridBagLayout();
		gbl_panel.columnWidths = new int[]{150, 39, 0};
		gbl_panel.rowHeights = new int[]{175, 0};
		gbl_panel.columnWeights = new double[]{0.0, 1.0, Double.MIN_VALUE};
		gbl_panel.rowWeights = new double[]{1.0, Double.MIN_VALUE};
		panel.setLayout(gbl_panel);
		BatchOperationComponent c = new BatchOperationComponent(r);
		c.setPreferredSize(new Dimension(150, 175));
		for(int i = 0; i < 20; i++) {
			final int j = i;
			batch.addRunnable(() -> {
				try {
					Thread.sleep(new Random().nextInt(0, 1000));
					System.out.println(Thread.currentThread() + ": Ran " + j);
				} catch (Throwable e) {
					throw new RuntimeException(e);
				}
			});
		}
		
		

		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		GridBagConstraints gbc_c = new GridBagConstraints();
		gbc_c.anchor = GridBagConstraints.NORTHWEST;
		gbc_c.insets = new Insets(0, 0, 0, 5);
		gbc_c.gridx = 0;
		gbc_c.gridy = 0;
		tabbedPane.add(c, gbc_c);
		GridBagConstraints gbc_tabbedPane = new GridBagConstraints();
		gbc_tabbedPane.anchor = GridBagConstraints.WEST;
		gbc_tabbedPane.gridx = 1;
		gbc_tabbedPane.gridy = 0;
		panel.add(tabbedPane, gbc_tabbedPane);
		GridBagConstraints gbc_panel = new GridBagConstraints();
		gbc_panel.fill = GridBagConstraints.BOTH;
		gbc_panel.gridx = 0;
		gbc_panel.gridy = 0;
		frame.getContentPane().add(panel, gbc_panel);
		frame.setVisible(true);
		new Thread(() -> {
			try {
				Thread.sleep(500);
				r.startBatch();
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
		}).start();
	}

}
