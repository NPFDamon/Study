package com.damon.algorithm;

import java.util.concurrent.atomic.AtomicInteger;

public class Test implements Runnable{

    int i = 0;
    @Override
    public void run() {
        while (true){
            synchronized (this){
                notifyAll();
                try {
                    Thread.currentThread();
                    Thread.sleep(100);
                }catch (Exception e){

                }
                if(i<= 10){
                    System.out.println(Thread.currentThread().getName() + ":" + i);
                    i++;
                    try {
                        wait();
                    }catch (Exception e){

                    }
                }
            }
        }
    }

    public static void main(String[] args){
        Test test = new Test();
        Thread t1 = new Thread(test);
        Thread t2 = new Thread(test);
        t1.setName("t1");
        t2.setName("t2");

        t1.start();
        t2.start();
    }
}
