package com.demo.Handler;

import com.demo.Connector;
import com.demo.HttpHeader;

public interface IHandlerable {
    void handler(Connector connector, HttpHeader httpHeader);
}
