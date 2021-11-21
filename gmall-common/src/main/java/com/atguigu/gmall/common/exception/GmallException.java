package com.atguigu.gmall.common.exception;

import lombok.Data;

public class GmallException extends RuntimeException{

    public GmallException(){
        super();
    }

    public GmallException(String msg){
        super(msg);
    }
}
