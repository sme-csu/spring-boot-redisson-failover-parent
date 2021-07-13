package com.microsoft.aa.haproxy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

@RestController
public class HaproxyHealthIndicator {

    private final static Logger LOGGER = LoggerFactory.getLogger(HaproxyHealthIndicator.class);



    @Autowired
    private HaproxyProperty haproxyProperty;

    @GetMapping(value = "/haproxy/healthcheck")
    public ResponseEntity getUser() throws IOException {

        if(isReachable(haproxyProperty.getHost(),haproxyProperty.getPort(),haproxyProperty.getTimeout())){
            return new ResponseEntity(HttpStatus.OK);
        }else
        {
            return new ResponseEntity(HttpStatus.SERVICE_UNAVAILABLE);
        }

    }



    private static boolean isReachable(String addr, int openPort, int timeOutMillis) {
        LOGGER.debug("host address: {}",addr);
        LOGGER.debug("port: {}",openPort);
        LOGGER.debug("timeout: {}",timeOutMillis);
        try {
            try (Socket soc = new Socket()) {
                soc.connect(new InetSocketAddress(addr, openPort), timeOutMillis);
            }
            LOGGER.info("health check success.");
            return true;
        } catch (Exception ex) {
            LOGGER.error("health check failed. The exception is {}",ex.toString());
            return false;
        }
    }
}
