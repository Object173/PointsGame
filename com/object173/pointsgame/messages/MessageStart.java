package com.object173.pointsgame.messages;

import com.object173.pointsgame.Options;

/**
 * Created by Ярослав on 12.01.2017.
 */

//сообщение начала игры (настроки: размеры поля и массив имен игроков)
public class MessageStart extends Message {
    protected int width,height;
    protected String[] players;

    public String[] getPlayers() {
        return players;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public MessageStart(int width, int height, String[] players)
    {
        super(Options.MESSAGE_START);

        this.width= width;
        this.height=height;
        this.players=players;
    }
}
