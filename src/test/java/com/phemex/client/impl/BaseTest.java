package com.phemex.client.impl;


import com.phemex.client.config.ConnectionConfig;
import com.phemex.client.impl.config.AppTestConfig;
import com.phemex.client.prop.ConnectionProp;
import com.phemex.client.prop.GrayProp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(classes = {AppTestConfig.class, ConnectionConfig.class})
@ActiveProfiles({"test"})
public class BaseTest {
    @Autowired
    protected GrayProp grayProp;
}
