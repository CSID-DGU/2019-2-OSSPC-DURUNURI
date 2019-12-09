package com.tetris.window;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Panel;

import javax.swing.JLabel;
import javax.swing.JPanel;



import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Timer;
import java.util.TimerTask;
import java.awt.BorderLayout;


public class stopwatch extends JPanel {
		
	private TetrisBoard board;
	// �����ڷ� �ޱ����� TetrisBoard�� ������
	
	public JLabel timeText;
	
	private TimeThread timeTh; 
	// �ð� ������
	
	private long time = 01, preTime = 01; 
		
	public stopwatch(TetrisBoard board, int x, int y, int width, int height)  {
		this.board = board;
		this.setBounds(x, y, width, height);
		// ��ġ, �ʺ�, ���̸� ����
		
		this.setLayout(new BorderLayout());
		
		this.add(centerPanel(), "Center");
		
	}
	// �����ڸ� �����.
	
	public Panel centerPanel(){
		GridBagLayout gridLayout = new GridBagLayout();
		GridBagConstraints constraints = new GridBagConstraints();
		this.setBackground(Color.black);
		// ����� ���� ������ ����
		
		constraints.anchor = GridBagConstraints.CENTER;
		constraints.weightx = 1;
		constraints.weighty = 1;
		Panel centerPanel = new Panel(gridLayout);
		
		timeText = new JLabel(toTime(time));
		timeText.setForeground(Color.red);
		timeText.setFont(new Font("Gothic", Font.BOLD, 10));
		constraints.gridx = 0;
		constraints.gridy = 1;
		centerPanel.add(timeText, constraints);

		JLabel title = new JLabel("Ÿ�̸� ����");
		title.setForeground(Color.white);
		title.setFont(new Font("Gothic", Font.BOLD, 10));
		constraints.gridx = 0;
		constraints.gridy = 0;
		centerPanel.add(title, constraints);

		return centerPanel;
		}
	
	private class TimeThread extends Thread{
		public void run() {
		try {
		while (!Thread.currentThread().isInterrupted()) {
		sleep(10);
		time = System.currentTimeMillis() - preTime;
		
		
		int m1 = (int)(time / 1000.0 / 60.0);
		int s1 = (int)(time % (1000.0 * 60) / 1000.0);
		//int ms1 = (int)(time % 1000 / 10.0);
		if (s1%2==1) {
			timeText.setForeground(Color.white);
		}else timeText.setForeground(Color.red);
		
		timeText.setText(toTime(time));
		}
		} catch (Exception e) {
		}
		}
		}
	// ms1�� �� ���̵��� ����
	// ���� �ʰ� ¦���� ������ ������ ���� ���Ѵ�.
	
	public void start(){
		
		if(timeTh == null || !timeTh.isAlive()){
		if(time != 0) preTime = System.currentTimeMillis() - time;
		else preTime = System.currentTimeMillis();
		timeTh = new TimeThread();
		timeTh.start();
			
		}	
	}
	// �����ġ�� �����ϴ� �ڵ�
	
	public void stop(){
		if(timeTh.isAlive()) {
		timeTh.interrupt();
		}
		time = 0l;
		timeText.setText(toTime(time));
	}
	
	public void prstop(){
		if(timeTh == null) return;
		if(timeTh.isAlive()) {
			timeTh.interrupt();
			}
		time = 0l;
		timeText.setText(toTime(time));
	}
	// ��Ʈ�������忡�� �ٽ� ���۹�ư�� ���� �� �ð��� �ʱ�ȭ �����ִ� �ڵ�
	
	public void pause(){
		if(timeTh == null) return;
		if(timeTh.isAlive()) timeTh.interrupt();
		}
	// �����⸦ ������ �� �ð��� �ʱ�ȭ�ϱ� ���� �ڵ�
	
	private String toTime(long time){
		int m = (int)(time / 1000.0 / 60.0);
		int s = (int)(time % (1000.0 * 60) / 1000.0);
		//int ms = (int)(time % 1000 / 10.0);
		
		return String.format("%02d : %02d ", m, s);
	}
	// ms�� �� ���̰� ����

}
