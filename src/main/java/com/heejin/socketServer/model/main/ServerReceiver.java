package com.heejin.socketServer.model.main;

import com.heejin.socketServer.model.Protocol;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.util.*;


public class ServerReceiver extends Thread{

    private Socket socket;

    private ObjectOutputStream oos;
    private ObjectInputStream ois;
    private BufferedWriter writer;
    private BufferedReader reader;

    private static final Logger logger = LogManager.getLogger(ServerReceiver.class);
    
    public ServerReceiver(Socket socket){
        this.socket = socket;
        try{
            ois = new ObjectInputStream(socket.getInputStream());
            oos = new ObjectOutputStream(socket.getOutputStream());
        }catch (IOException e){
            e.printStackTrace();
        }
    }
    

    @Override
    public void run() {

        boolean isStop = false;
        if (ois == null) { //무한루프 방지
            isStop = true;
        }

        try {
            run_start://while문같은 반복문 전체를 빠져 나가도록 처리할 때
            while (!isStop) {

                String msg = ois.readObject().toString(); //클라이언트로 부터 오는 메세지 수신 담당, 항상 메세지를 받은 이후부터 모든 서버업무가 수행이 가능함
				logger.info("{} - 메세지를 수신했습니다.", msg);

				oos.writeObject("테스트 완료");

			}
        } catch (Exception e) {
            e.printStackTrace();
        }finally {

        }
    }
    
	public boolean validateUser(String id, String pw){
		List<Map<String, String>> userList = getUserList();
		
		for(Iterator<Map<String, String>> iter = userList.iterator(); iter.hasNext();) {
			Map<String, String> element = iter.next();
			
			if(element.get(id).contains(id)) {
				if(element.get(pw).contains(pw)) {
					return true;
				}
			} else {
				return false;
			}
		}
		return false;

	}

	public List<Map<String, String>> getUserList(){
		List<Map<String, String>> list = new ArrayList<>();


		return list;
	}
}
