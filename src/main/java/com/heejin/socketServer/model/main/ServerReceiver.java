package com.heejin.socketServer.model.main;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


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

//                String msg = ois.readObject().toString(); //클라이언트로 부터 오는 메세지 수신 담당, 항상 메세지를 받은 이후부터 모든 서버업무가 수행이 가능함
				Map<String, String> map = (Map<String, String>) ois.readObject();
				String id = map.get("id");
				String pwd = map.get("pwd");

				int userCheck = userCheck(id, pwd);

				if(userCheck == 1) {
					oos.writeObject("로그인 성공");
				}else {
					oos.writeObject("로그인 실패");
				}

				oos.writeObject(id);

			}
        } catch (Exception e) {
            e.printStackTrace();
        }finally {

        }
    }

    public int userCheck(String id, String pwd) {
    	int userCheck = 0;

    	try {
            File file = new File("C:/Users/ISPARK/git/socketServer/src/main/resources/user-info.xml");
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document document = db.parse(file);
            document.getDocumentElement().normalize();
            logger.info("Root Element :{}", document.getDocumentElement().getNodeName());
            NodeList nList = document.getElementsByTagName("user");
            logger.info("----------------------------");
            for (int temp = 0; temp < nList.getLength(); temp++) {
                Node nNode = nList.item(temp);
                logger.info("Current Element :{}", nNode.getNodeName());
                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) nNode;
                    if(eElement.getElementsByTagName("id").item(0).getTextContent().equalsIgnoreCase(id)) {
                    	if(eElement.getElementsByTagName("pw").item(0).getTextContent().equalsIgnoreCase(pwd)) {
                    		userCheck = 1;
                    		break;
                    	}
                    }else {
                    	userCheck = 0;
                    }
                }
            }
        }
        catch(Exception e) {
            logger.info("e: {}", e);
        }

    	return userCheck;
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
