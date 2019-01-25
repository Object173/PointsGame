package com.object173.pointsgame.view;

import com.object173.pointsgame.Options;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

/**
 * Created by Ярослав on 14.01.2017.
 */

//диалог реультатов игры
public class WinnerDialog extends JDialog {

    private JPanel panel; //основная панель

    private JLabel statusLabel; //строка заголовок
    private JLabel[] playerLabels; //лейблы для игроков

    private ArrayList<String> playernames; //коллекция имен игроков
    private int[] score; //массив баллов игроков
    //конструктор
    public WinnerDialog(GameForm parent, ArrayList<String> playernames, int[] score)
    {
        super(parent); //устанавливаем родительское окно

        this.setSize(150,200); //устанавливаем размер окна
        this.setTitle("Результат"); //заголовок окна
        this.playernames=playernames;
        this.score=score;

        createGUI(); //добавляем элементы интерфейса
    }
    //добавление элементов интерфейса
    private void createGUI()
    {
        panel=new JPanel(new GridLayout(5,1));
        this.add(panel);

        statusLabel=new JLabel("Рейтинг игры");
        panel.add(statusLabel);

        playerLabels=new JLabel[playernames.size()]; //создаем коллекцию лейблов игроков
        //заполяем лейблы игроков в порядке убывания баллов
        for(int i=0;i<playernames.size();i++)
        {
            int max=0; //индекс игрока с максимальным баллом
            for(int j=1;j<playernames.size();j++)
                if(score[j]>score[max]) max=j;
            playerLabels[i]=new JLabel(playernames.get(max)+": "+score[max]); //берем значения имени игрока и баллов
            //выставляем цвет игрока
            playerLabels[i].setForeground(Options.getInstance().getPlayerColor(Options.getInstance().getPlayerTag(max)));
            panel.add(playerLabels[i]);
            score[max]=-1; //чтобы второй раз не выводился игрко
        }
    }
}
