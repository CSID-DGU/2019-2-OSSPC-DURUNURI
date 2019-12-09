package com.tetris.window;

import com.tetris.main.Music;
import com.tetris.main.TetrisMain;

import static com.tetris.window.TetrisBoard.usingBGM;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

public class Sound {

	public static Music GameMusic;
	public static Music GameEndSound;

	public void GameMusicStart() {

		if (GameMusic != null && GameMusic.isAlive()) {
			GameMusic.close();
			if (usingBGM) {
				GameMusic = new Music("Tetris.mp3", true);
				GameMusic.start();

			}
		} else {
			if (usingBGM) {
				GameMusic = new Music("Tetris.mp3", true);
				GameMusic.start();
			}
		}
		return;
	}

	public void GameExit() {
		if (GameMusic != null && GameMusic.isAlive()) {
			GameMusic.close();
		}
		return;
	}

	public void GameOver() {
		if (GameMusic != null && GameMusic.isAlive()) {
			GameMusic.close();
		}
		GameEndSound = new Music("Crash.mp3", false);
		GameEndSound.start();
		 ImageIcon popupicon = new
		 ImageIcon(TetrisMain.class.getResource("../../../Images/gameover.PNG"));
		 JOptionPane.showMessageDialog(null, null, "Game Over",
		 JOptionPane.ERROR_MESSAGE, popupicon);
	}

}