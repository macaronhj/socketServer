package com.heejin.socketServer.service;


import com.heejin.socketServer.model.UserVO;

import java.util.List;
import java.util.Map;

public interface DataService {

    List<Map<String, String>> getUserList();

    //xml을 읽고 id list 가져오기
    List<String> getIdList();

    //xml을 읽고 입력받은 id이 있는지 조회
    boolean existId(String id);

    //xml을 읽고 입력받은 id와 pw가 일치하는지 조회
    int existLogin(UserVO user);

    int existLogin(String id, String pw);

    //xml에 id, pw 추가
    int insertUser();
}
