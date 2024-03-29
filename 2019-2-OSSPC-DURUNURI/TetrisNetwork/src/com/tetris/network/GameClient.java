package com.tetris.network;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

import com.tetris.classes.Block;
import com.tetris.classes.TetrisBlock;
import com.tetris.window.Tetris;

//---------------------[ 클라이언트 ]---------------------
public class GameClient implements Runnable{
	private Tetris tetris;
	private Socket socket;
	private ObjectOutputStream oos;
	private ObjectInputStream ois;

	//서버 IP
	private String ip;
	private int port;
	private String name;
	private int index;
	private boolean isPlay;
	
	//생성자
	public GameClient(Tetris tetris,String ip, int port, String name){
		this.tetris = tetris;
		this.ip = ip;
		this.port = port;
		this.name = name;
	}//GameClient()

	public boolean start(){
		return this.execute();	
	}

	//소켓 & IO 처리
	public boolean execute(){
		try{
			socket = new Socket(ip,port);
			ip = InetAddress.getLocalHost().getHostAddress();
			oos = new ObjectOutputStream(socket.getOutputStream());
			ois = new ObjectInputStream(socket.getInputStream());
			System.out.println("클라이언트가 실행 중입니다.");
		}catch(UnknownHostException e){
			e.printStackTrace();
			return false;
		}catch(IOException e){
			e.printStackTrace();
			return false;
		}

		tetris.getBoard().clearMessage();
		
		//이름보내기
		DataShip data = new DataShip();
		data.setIp(ip);
		data.setName(name);
		send(data);
		
		//리스트받아오기
		printSystemMessage(DataShip.PRINT_SYSTEM_OPEN_MESSAGE);
		//리스트에 추가하기
		printSystemMessage(DataShip.PRINT_SYSTEM_ADDMEMBER_MESSAGE);
		//인덱스받아오기
		setIndex();
		//스레드
		Thread t = new Thread(this);
		t.start();
		System.out.println(this.index);
		
		return true;
	}//execute()

	
	//Run : 서버의 명령을 기다림.
	public void run(){
		DataShip data = null;
		while(true){
			try{
				data = (DataShip)ois.readObject(); 
			}catch(IOException e){e.printStackTrace();break;
			}catch(ClassNotFoundException e){e.printStackTrace();
			}
			


			//서버로부터 DataShip Object를 받아옴.
			if(data == null) continue;
			if(data.getCommand() == DataShip.CLOSE_NETWORK){
				reCloseNetwork();
				break;
			}else if(data.getCommand() == DataShip.SERVER_EXIT){
				closeNetwork(false);
			}else if(data.getCommand() == DataShip.GAME_START){
				reGameStart(data.isPlay(), data.getMsg(), data.getSpeed());
			}else if(data.getCommand() == DataShip.ADD_BLOCK){
				if(isPlay)reAddBlock(data.getMsg(), data.getNumOfBlock(), data.getIndex());
				if(index != data.getIndex()) {
					//tetris.getBoard().removeLineImage(); // 공격 시 이미지 HK
				}
			}else if(data.getCommand() == DataShip.SET_INDEX){
				reSetIndex(data.getIndex());
			}else if(data.getCommand() == DataShip.GAME_OVER){
				if(index == data.getIndex()) {
					isPlay = data.isPlay();
					tetris.getBoard().lose();
				}
				else {
					tetris.getBoard().win();
				}
				reGameover(data.getMsg(), data.getTotalAdd());
			}else if(data.getCommand() == DataShip.PRINT_MESSAGE){
				rePrintMessage(data.getMsg());
			}else if(data.getCommand() == DataShip.PRINT_SYSTEM_MESSAGE){
				rePrintSystemMessage(data.getMsg());
			}else if(data.getCommand() == DataShip.GAME_WIN){
				rePrintSystemMessage(data.getMsg()+"\nTOTAL ADD : "+data.getTotalAdd());
				tetris.getBoard().setPlay(false);
			}else if(data.getCommand() == DataShip.DRAW_BLOCK_SHAP) {		//HK
				// 내가 보낸 요청이 아니었을 경우(상대방 플레이어의 블록이 이동한 경우) 화면에 그린다.
				if(data.getPlayer() != this.index) {
					reDrawBlockShap(data.getShap());
					tetris.getBoard().setShap(data.getShap());
				}
			}else if(data.getCommand() == DataShip.DRAW_BLOCK_DEPOSIT) {		//HK
				if(data.getPlayer() != this.index) {
					reDrawBlockDeposit(data.getDeposit());
					tetris.getBoard().setDeposit(data.getDeposit());
				}
			}else if(data.getCommand() == DataShip.DRAW_BLOCK_HOLD) {		//HK
				if(data.getPlayer() != this.index) {
					reDrawBlockHold(data.getHold());
					tetris.getBoard().setHold(data.getHold());
				}
			}else if(data.getCommand() == DataShip.DRAW_BLOCK_NEXT) {		//HK
				if(data.getPlayer() != this.index) {
					reDrawBlockNext(data.getNext());
					tetris.getBoard().setNext(data.getNext());
				}
			}
			
		}//while(true)
		
		
	}//run()


	// 서버에게 요청함
	public void send(DataShip data){
		try{
			oos.writeObject(data); 
			oos.flush();
		}catch(IOException e){
			e.printStackTrace();
		}
	}//sendData()
	
	
	
	
	//요청하기 : 상대블록 그리기 HK
	public void drawBlockShap(TetrisBlock shap) {
		DataShip data = new DataShip(DataShip.DRAW_BLOCK_SHAP);
		data.setShap(shap);
		data.setPlayer(index);
		send(data);
		System.out.println("client : "+shap.getPosX()+","+shap.getPosY());
		
		  try{ oos.reset(); //블록의 좌표를 업데이트한다
		  }catch(IOException e){
		  e.printStackTrace(); }
		 
	}
	public void reDrawBlockShap(TetrisBlock shap) {
		//tetris.getBoard().drawBlockShap(shap); by in
	}//drawBlockShap HK
	
	public void drawBlockDeposit(ArrayList<Block> blockList2) {
		DataShip data = new DataShip(DataShip.DRAW_BLOCK_DEPOSIT);
		data.setDeposit(blockList2);
		data.setPlayer(index);
		send(data);
		
		  try{ oos.reset(); //블록의 좌표를 업데이트한다.
		  }catch(IOException e){
		  e.printStackTrace(); }
	}
	public void reDrawBlockDeposit(ArrayList<Block> blockList2) {
		tetris.getBoard().drawBlockDeposit(blockList2);
	}//drawBlockDeposit HK
	
	public void drawBlockHold(TetrisBlock hold) {
		DataShip data = new DataShip(DataShip.DRAW_BLOCK_HOLD);
		data.setHold(hold);
		data.setPlayer(index);
		send(data);
		
		  try{ oos.reset(); //블록의 좌표를 업데이트한다. 
		  }catch(IOException e){
		  e.printStackTrace(); }
		 
	}
	public void reDrawBlockHold(TetrisBlock hold) {
		tetris.getBoard().drawBlockHold(hold);
	}//drawBlockHold HK
	
	public void drawBlockNext(ArrayList<TetrisBlock> nextBlocks) {
		DataShip data = new DataShip(DataShip.DRAW_BLOCK_NEXT);
		data.setNext(nextBlocks);
		data.setPlayer(index);
		send(data);
		
		  try{ oos.reset(); //블록의 좌표를 업데이트한다. 
		  }catch(IOException e){
		  e.printStackTrace(); }
		 
	}
	public void reDrawBlockNext(ArrayList<TetrisBlock> nextBlocks) {
		tetris.getBoard().drawBlockNext(nextBlocks);
	}//drawBlockNext HK

	
	//요청하기 : 연결끊기
	public void closeNetwork(boolean isServer){
		DataShip data = new DataShip(DataShip.CLOSE_NETWORK);
		if(isServer) data.setCommand(DataShip.SERVER_EXIT);
		send(data);
	}
	//실행하기 : 연결끊기
	public void reCloseNetwork(){

		tetris.closeNetwork();
		try {
			ois.close();
			oos.close();
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	//요청하기 : 게임시작
	public void gameStart(int speed){
		DataShip data = new DataShip(DataShip.GAME_START);
		data.setSpeed(speed);
		send(data);
	}
	//실행하기 : 게임시작
	public void reGameStart(boolean isPlay, String msg, int speed){
		this.isPlay = isPlay;
		tetris.gameStart(speed);
		rePrintSystemMessage(msg);
	}
	//요청하기 : 메시지
	public void printSystemMessage(int cmd){
		DataShip data = new DataShip(cmd);
		send(data);
	}
	//실행하기 : 메시지
	public void rePrintSystemMessage(String msg){
		tetris.printSystemMessage(msg);
	}
	public void addBlock(int numOfBlock){
		DataShip data = new DataShip(DataShip.ADD_BLOCK);
		data.setNumOfBlock(numOfBlock);
		send(data);
	}
	public void reAddBlock(String msg, int numOfBlock, int index){
		if(index != this.index) {
			tetris.getBoard().addBlockLine(numOfBlock);
			// 만약 속도가 15 이상이라면 최고 속도로 고정한다. HK
			if(tetris.getBoard().getComboSpeed().getSelectedIndex() >= 14) {
				tetris.getBoard().getComboSpeed().setSelectedIndex(19);
			}
			// 속도가 15보다 낮으면 속도를 5만큼 올린다. HK
			else { 
			Integer speed = tetris.getBoard().getComboSpeed().getSelectedIndex() + 5;
			tetris.getBoard().getComboSpeed().setSelectedIndex(speed);
			System.out.println(tetris.getBoard().getComboSpeed().getSelectedIndex()); // Test HK
			}
		}
		rePrintSystemMessage(msg);
	}
	
	
	public void setIndex(){
		DataShip data = new DataShip(DataShip.SET_INDEX);
		send(data);
	}
	public void reSetIndex(int index){
		this.index = index;
	}
	
	//요청하기 : 게임종료
	public void gameover(){
		DataShip data = new DataShip(DataShip.GAME_OVER);
		send(data);
	}
	public void reGameover(String msg, int totalAdd){
		tetris.printSystemMessage(msg);
		tetris.printSystemMessage("TOTAL ADD : "+totalAdd);
	}
	public void printMessage(String msg){
		DataShip data = new DataShip(DataShip.PRINT_MESSAGE);
		data.setMsg(msg);
		send(data);
	}
	public void rePrintMessage(String msg){
		tetris.printMessage(msg);
	}
	public void reChangSpeed(Integer speed) {
		tetris.changeSpeed(speed);
	}
}
