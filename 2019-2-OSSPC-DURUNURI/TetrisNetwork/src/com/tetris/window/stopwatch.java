package com.tetris.window;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Panel;

import javax.swing.JLabel;
import javax.swing.JPanel;



public class stopwatch extends JPanel {
	private static final long serialVersionUID = 1L;
	
	private TetrisBoard board;
	// 생성자로 받기위해 TetrisBoard를 가져옴
	
	public JLabel timeText;
	public JLabel score; 
	
	private TimeThread timeTh; 
	// 시간 쓰레드
	
	private long time = 01, preTime = 01; 
	private int speedcheck, speedcheck1 = 0;
		
	public stopwatch(TetrisBoard board, int x, int y, int width, int height)  {
		this.board = board;
		this.speedcheck=0; this.speedcheck1=30;
		this.setBounds(x, y, width, height);
		// 위치, 너비, 넓이를 정함
		
		this.setLayout(new BorderLayout());
		
		this.add(centerPanel(), "Center");
		
	}
	// 생성자를 만든다.
	
	public Panel centerPanel(){
		GridBagLayout gridLayout = new GridBagLayout();
		GridBagConstraints constraints = new GridBagConstraints();
		this.setBackground(Color.black);
		// 배경을 검은 색으로 설정
		
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

		score = new JLabel(toScore());
		score.setForeground(Color.white);
		score.setFont(new Font("Gothic", Font.BOLD, 10));
		constraints.gridx = 0;
		constraints.gridy = 0;
		centerPanel.add(score, constraints);

		return centerPanel;
		}
	
	private class TimeThread extends Thread{
		public void run() {
		try {
		while (!Thread.currentThread().isInterrupted()) {
		sleep(10);
		time = System.currentTimeMillis() - preTime;
		
		
		//int m1 = (int)(time / 1000.0 / 60.0);
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
	// ms1은 안 보이도록 설정
	// 현재 초가 짝수일 때마다 글자의 색이 변한다.
	
	public void start(){
		
		if(timeTh == null || !timeTh.isAlive()){
		if(time != 0) preTime = System.currentTimeMillis() - time;
		else preTime = System.currentTimeMillis();
		timeTh = new TimeThread();
		timeTh.start();
			
		}	
	}
	// 스톱워치를 시작하는 코드
	
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
	// 테트리스보드에서 다시 시작버튼을 누를 때 시간을 초기화 시켜주는 코드
	
	public void pause(){
		if(timeTh == null) return;
		if(timeTh.isAlive()) timeTh.interrupt();
		}
	// 나가기를 눌렀을 때 시간을 초기화하기 위한 코드
	public void checkScore() {
		score.setText(toScore());
	}
	
	private String toScore() {
		return ("score "+board.getScore());	
	}
	
	private String toTime(long time){
		int m = (int)(time / 1000.0 / 60.0);
		int s = (int)(time % (1000.0 * 60) / 1000.0);
		//int ms = (int)(time % 1000 / 10.0);
		
		if(m-speedcheck!=0) {
			System.out.println("60초");
			board.speedup();
			speedcheck=m;
			speedcheck1=30;
		} else if(s-speedcheck1==0) {
			System.out.println("30초");
			board.speedup();
			speedcheck1=0;
		}
		return String.format("%02d : %02d ", m, s);
	}
	// ms을 안 보이게 숨김

}

