package com.gamebuster19901.excite.modding.ui;

import java.awt.Dimension;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import com.gamebuster19901.excite.modding.concurrent.Batch;
import com.gamebuster19901.excite.modding.concurrent.BatchRunner;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;

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
		BatchOperationComponent c = new BatchOperationComponent(r);
		c.setPreferredSize(new Dimension(150, 175));
		for(int i = 0; i < 1000; i++) {
			final int j = i;
			batch.addRunnable(() -> {
				try {
					System.out.println(Thread.currentThread() + ": Ran " + j);
					SwingUtilities.invokeLater(() -> {
						c.update();
						System.out.println("Updated!");
					});
					
				} catch (Throwable e) {
					throw new RuntimeException(e);
				}
			});
		}
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{450, 0};
		gridBagLayout.rowHeights = new int[]{263, 0};
		gridBagLayout.columnWeights = new double[]{1.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{1.0, Double.MIN_VALUE};
		frame.getContentPane().setLayout(gridBagLayout);
		JPanel panel = new JPanel();
		
		
		panel.add(c);
		panel.setLayout(new WrapLayout());
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
