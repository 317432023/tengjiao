package com.tengjiao.part.wx.mp.model;


import java.io.Serializable;

/**
 * @author tengjiao
 * @description
 * @date 2021/12/7 22:43
 */
public class QRcodeResponse implements Serializable {
    private static final long serialVersionUID = 1866904517027398055L;

    private int code;
    private byte[] bytes;
    private String text;

    public int getCode() {
        return code;
    }

    public QRcodeResponse setCode(int code) {
        this.code = code;
        return this;
    }

    public byte[] getBytes() {
        return bytes;
    }

    public QRcodeResponse setBytes(byte[] bytes) {
        this.bytes = bytes;
        return this;
    }

    public String getText() {
        return text;
    }

    public QRcodeResponse setText(String text) {
        this.text = text;
        return this;
    }



}
