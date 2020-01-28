package com.jeffreyojala.twitterstream.http;


import com.jeffreyojala.twitterstream.services.IncomingTwitterSampleStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Control {

    @Autowired
    private IncomingTwitterSampleStream stream;

    @GetMapping("/start")
    public void greeting() {
        stream.consume();
    }
}
