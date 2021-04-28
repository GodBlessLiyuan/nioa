package com.itheima._02bio04;

import java.io.DataInputStream;
import java.io.IOException;

public class DataInTest {
    public static void main(String[] args) throws IOException {
       DataInputStream dis = new DataInputStream(System.in);
       while (true){
//           int i = dis.readInt();
//           System.out.println(i);

           String s = dis.readUTF();
           System.out.println(s);
       }
    }
}
