package com.object173.pointsgame.view;

import com.object173.pointsgame.Options;
import com.object173.pointsgame.events.EventProducer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Created by Ярослав on 30.12.2016.
 */
//класс клетки игрового поля для отображения
public class Cell extends JButton {
    private int cellX,cellY; //координаты на поле
    private int tag=0; //текущая метка игрока

    public int getTag() {
        return tag;
    } //получение метки игрока

    //установка метки игрока
    public void setTag(int tag) {
        this.tag = tag;
        this.setBackground(Options.getInstance().getPlayerColor(tag)); //меняем цвет на цвет игрока
        this.setEnabled(this.tag==0); //если установлена метка не пустой клетки, то блокируем
    }
    //конструктор
    public Cell(int x, int y)
    {
        super();

        this.cellX=x; //задаем координаты
        this.cellY=y;

        //обработка нажатия
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if(isEnabled())
                    EventProducer.firePlayerRequestMove(x,y); //вызвыаем запрос насовершение хода

            }
        });
    }

    //установка цвета
    public void setColor(Color color){
        this.setBackground(color);
        repaint();
    }
}
