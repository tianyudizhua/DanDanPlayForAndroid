package com.xyoye.dandanplay.bean;

/**
 * Created by xyoye on 2019/8/7.
 */

public class FeedbackBean {
    private String header;
    private String content;

    private boolean open;

    public FeedbackBean(String header, String content) {
        this.header = header;
        this.content = content;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }
}
