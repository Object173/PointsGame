package com.object173.pointsgame.messages;

import java.io.Serializable;

/**
 * Created by Ярослав on 20.12.2016.
 */

//базовый класс сообщений, имеет только текст сообщения
public class Message implements Serializable{
    protected String message;

    public String getMessage() {return message;}

    public Message(String message)
    {
        this.message=message;
    }
}

