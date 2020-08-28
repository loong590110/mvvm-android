package com.mylive.live.utils;

import org.junit.Test;

import static org.junit.Assert.*;

public class CantorTest {

    @Test
    public void getCantor() {
        long i = Cantor.getCantor(1, 1);
        System.out.println("1/1 -> " + i);
        i = Cantor.getCantor(1, 2);
        System.out.println("1/2 -> " + i);
        i = Cantor.getCantor(2, 1);
        System.out.println("2/1 -> " + i);
    }

    @Test
    public void reverseCantor() {
        long[] ia = new long[2];
        Cantor.reverseCantor(1, ia);
        System.out.println(String.format("1 -> %d/%d", ia[0], ia[1]));
        Cantor.reverseCantor(2, ia);
        System.out.println(String.format("2 -> %d/%d", ia[0], ia[1]));
        Cantor.reverseCantor(3, ia);
        System.out.println(String.format("3 -> %d/%d", ia[0], ia[1]));
        Cantor.reverseCantor(8, ia);
        System.out.println(String.format("8 -> %d/%d", ia[0], ia[1]));
    }
}