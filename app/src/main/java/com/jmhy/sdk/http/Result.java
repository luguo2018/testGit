package com.jmhy.sdk.http;

public class Result<T> {
    public int code;
    public String message;
    public T data;

    public boolean isSuccess(){
        return code==200;
    }
    public T getData() {
        return data;
    }

    @Override
    public String toString() {
        return "Result{" +
                "code=" + code +
                ", message='" + message + '\'' +
                ", data=" + data +
                '}';
    }
}
