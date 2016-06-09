package com.cffex.chapter5.commons;

import java.io.Serializable;

/**
 * Created by Ming on 2016/6/1.
 */
public class Add  implements Serializable{
    private int num1;
    private int num2;

    public Add(int num1,int num2){
        this.num1=num1;
        this.num2=num2;
    }

    public int getNum1() {
        return num1;
    }

    public void setNum1(int num1) {
        this.num1 = num1;
    }

    public int getNum2() {
        return num2;
    }

    public void setNum2(int num2) {
        this.num2 = num2;
    }
}
