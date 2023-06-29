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

import com.heejin.socketServer.model.Protocol;
import com.heejin.socketServer.service.UserService;
import com.heejin.socketServer.service.impl.XmlUserService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


public class ServerReceiver extends Thread{

    private static final Logger logger = LogManager.getLogger(ServerReceiver.class);

    private final UserService userService = new XmlUserService();

    private final Socket socket;

    private ObjectOutputStream oos;
    private ObjectInputStream ois;

	private List<ServerReceiver> onlineList;
    private List<String> onlineUserList;

    private String userId;


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

    //로그인한 사람의 id값
    public String onlineUserId() throws ClassNotFoundException, IOException {
		return getOnlineUserId();
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

                        if(userCheck == 1){ //1일떄 로그인 성공
                            String reply = Protocol.checkLogin + Protocol.seperator + id + Protocol.seperator + "Y" + Protocol.seperator + "jklee,jsj00023, huhhj";
                            oos.writeObject(reply);
                        }

                        break;
                    case Protocol.showUser:

                        break;

                    default:
                        logger.info("프로토콜 없는 메세지");
                }

				String id = getOnlineUserId();
				String pwd = getOnlineUserPwd();
				logger.info("id: {}",id);
				logger.info("pwd: {}",pwd);

				int userCheck = userService.existLogin(id, pwd);

				if(userCheck == 1) {

                    //로그인한 유저 목록 뽑기
                    onlineUserList.add(id);
                    logger.info("onlineUser: {}", onlineUserList);

					oos.writeObject("Y");
					oos.writeObject(id);
					logger.info("----------------------------");
				}else {
					oos.writeObject("N");
				}

				oos.writeObject(id);
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

	public Map<String, String> getMap() throws ClassNotFoundException, IOException{
		return (Map<String, String>) ois.readObject();
	}

	public String getOnlineUserId() throws ClassNotFoundException, IOException {
		return getMap().get("id");
	}

	public String getOnlineUserPwd() throws ClassNotFoundException, IOException {
		return getMap().get("pwd");
	}

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
