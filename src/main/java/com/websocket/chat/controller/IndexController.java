package com.websocket.chat.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.annotation.PostConstruct;

@Controller
public class IndexController {
    @GetMapping({"","/index"})
    public String index() {
        return "redirect:/chat/room";
    }


    @PostConstruct
    public void init(){

        /*
        1. 기존 대화방 및 대화 삭제
        2. 대화방 2개 개설
         - user-admin
         - user-guest
        3. 대화 생성 및 저장

         */

    }

}
