package hello.liveboardgame.controller;

import hello.liveboardgame.Greeting;
import hello.liveboardgame.HelloMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.util.HtmlUtils;

import java.util.Map;

@Controller
@Slf4j
public class StompController {
//    @MessageMapping("/hello")
//    @SendTo("/topic/greetings")
    @MessageMapping("/sendMessage")
    @SendTo("/topic/messages")
    public Greeting greeting(HelloMessage/*Map<String, String>*/ message) throws Exception {
//        log.info("message={}",message.get("text"));
        log.info("message={}",message.getName());
        Thread.sleep(1000); // simulated delay
//        return new Greeting("Hello, " + HtmlUtils.htmlEscape(message.get("text")) + "!");
        return new Greeting("Hello, " + HtmlUtils.htmlEscape(message.getName()) + "!");
    }
//    @MessageMapping("/msg")
//    @SendTo("/topic/msg")
//    public String stompMessageTest(String msg) {
//        log.info("msg>>{}", msg);
//        return msg;
//    }
}
