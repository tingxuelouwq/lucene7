package com.kevin.chap7.digester;

/**
 * @类名: DocumentHandlerException
 * @包名：com.kevin.chap7
 * @作者：kevin[wangqi2017@xinhua.org]
 * @时间：2018/7/26 14:32
 * @版本：1.0
 * @描述：
 */
public class DocumentHandlerException extends Exception {

    public DocumentHandlerException() {
    }

    public DocumentHandlerException(String message) {
        super(message);
    }

    public DocumentHandlerException(String message, Throwable cause) {
        super(message, cause);
    }

    public DocumentHandlerException(Throwable cause) {
        super(cause);
    }
}
