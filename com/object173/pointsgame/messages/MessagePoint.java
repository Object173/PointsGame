package com.object173.pointsgame.messages;

import com.object173.pointsgame.Options;

/**
 * Created by Ярослав on 13.01.2017.
 */

//сообщение с данными хода (координаты клетки и метка игрока)
public class MessagePoint extends Message {
    private int x;
    private int y;
    private int id;

    public int getId() {
        return id;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public MessagePoint(int x, int y, int id) {
        super(Options.MESSAGE_MOVE);

        this.x=x;
        this.y=y;
        this.id=id;
    }
}
