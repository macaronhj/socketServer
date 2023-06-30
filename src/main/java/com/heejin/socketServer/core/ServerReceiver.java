package com.heejin.socketServer.core;

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

import com.heejin.socketServer.model.Protocol;
import com.heejin.socketServer.service.UserService;
import com.heejin.socketServer.service.impl.XmlUserService;


public class ServerReceiver extends Thread{

    private static final Logger logger = LogManager.getLogger(ServerReceiver.class);

    private final UserService userService = new XmlUserService();

    private final Socket socket;

    private ObjectOutputStream oos;
    private ObjectInputStream ois;

	private List<ServerReceiver> onlineList;
    private List<String> onlineUserList;


    public ServerReceiver(Socket socket, List<ServerReceiver> onlineList){
        this.socket = socket;
        this.onlineList = onlineList;
        try{
            ois = new ObjectInputStream(socket.getInputStream());
            oos = new ObjectOutputStream(socket.getOutputStream());
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public ServerReceiver(Socket socket, List<ServerReceiver> onlineList, List<String> onlineUserList){
        this.socket = socket;
        this.onlineList = onlineList;
        this.onlineUserList = onlineUserList;
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

                String msg = ois.readObject().toString();
                String[] arr = msg.split(Protocol.seperator);
            	logger.info("수신 메세지 : {}", arr);

                switch (arr[0]){
                    case Protocol.checkLogin:
                        String id = arr[1];
                        String pw = arr[2];
                        int userCheck = userService.existLogin(id, pw);

                        String onlineUser = getOnlineUser(id);

                        if(userCheck == 1){ //1일떄 로그인 성공
                            String reply = Protocol.checkLogin + Protocol.seperator + id + Protocol.seperator + "Y" + Protocol.seperator + onlineUser;
                            logger.info("reply: {}",reply);
                            oos.writeObject(reply);
                        }

                        break;
                    case Protocol.showUser:

                        break;

                    default:
                        logger.info("프로토콜 없는 메세지");
                }

			}
        } catch (Exception e) {
            logger.error("서버리시버 에러 발생");
            e.printStackTrace();
        }finally {

        }
    }

    //유저 확인
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

            for (int temp = 0; temp < nList.getLength(); temp++) {
                Node nNode = nList.item(temp);
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

	//로그인한 유저 목록 뽑기
	public String getOnlineUser(String id){
        onlineUserList.add(id);
        String onlineUser = "";
        if( onlineUserList.size() > 1 ) {
        	for (String item : onlineUserList) {
        		onlineUser += item + ",";
        	}
        	onlineUser = onlineUser.substring(0, onlineUser.length()-1);
        }else if( onlineUserList.size() == 1 ) {
        	onlineUser = onlineUserList.get(0);
        }
        return onlineUser;
	}

}
