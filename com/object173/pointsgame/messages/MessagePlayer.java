package com.object173.pointsgame.messages;

/**
 * Created by Ярослав on 12.01.2017.
 */

//сообщение с данными игрока (id и имя)
public class MessagePlayer extends Message {
    protected String player;
    protected int id;

    public int getId() {
        return id;
    }

    public String getPlayer() {
        return player;
    }

    public MessagePlayer(String message, String player, int id)
    {
        super(message);

        this.player=player;
        this.id=id;
    }
}
