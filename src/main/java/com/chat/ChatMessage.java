package com.chat;

public class ChatMessage {

    private String type;
    private String username;
    private String text;
    private String timestamp;
    private int count;

    public ChatMessage() {}

    public ChatMessage(String type, String text, int count) {
        this.type  = type;
        this.text  = text;
        this.count = count;
    }

    public String getType()                    { return type; }
    public void   setType(String type)         { this.type = type; }

    public String getUsername()                { return username; }
    public void   setUsername(String username) { this.username = username; }

    public String getText()                    { return text; }
    public void   setText(String text)         { this.text = text; }

    public String getTimestamp()               { return timestamp; }
    public void   setTimestamp(String ts)      { this.timestamp = ts; }

    public int  getCount()                     { return count; }
    public void setCount(int count)            { this.count = count; }
}
