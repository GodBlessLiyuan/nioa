package com.itheima._02bio04;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ServerChat {
    
    /** 定义一个集合存放所有在线的socket  */
	public static Map<Socket, String> onLineSockets = new HashMap<>();

   public static void main(String[] args) {
      try {
         /** 1.注册端口   */
         ServerSocket serverSocket = new ServerSocket(9999);

         /** 2.循环一直等待所有可能的客户端连接 */
         while(true){
            Socket socket = serverSocket.accept();
            /**3. 把客户端的socket管道单独配置一个线程来处理 */
            new ServerReader(socket).start();
         }
      } catch (Exception e) {
         e.printStackTrace();
      }
   }
}

class ServerReader extends Thread {
   private Socket socket;
   public ServerReader(Socket socket) {
      this.socket = socket;
   }
   @Override
   public void run() {
      DataInputStream dis = null;
      try {
         dis = new DataInputStream(socket.getInputStream());
         /** 1.循环一直等待客户端的消息 */
         while(true){
            /** 2.读取当前的消息类型 ：登录,群发,私聊 , @消息 */
            int flag = dis.readInt();
            if(flag == 1){
               /** 先将当前登录的客户端socket存到在线人数的socket集合中   */
               String name = dis.readUTF() ;
               System.out.println(name+"---->"+socket.getRemoteSocketAddress());
               ServerChat.onLineSockets.put(socket, name);
            }
            writeMsg(flag,dis);
         }
      } catch (Exception e) {
         System.out.println("--有人下线了--");
         // 从在线人数中将当前socket移出去
         ServerChat.onLineSockets.remove(socket);
         try {
            // 从新更新在线人数并发给所有客户端
            writeMsg(1,dis);
         } catch (Exception e1) {
            e1.printStackTrace();
         }
      }

   }

   private void writeMsg(int flag, DataInputStream dis) throws Exception {
      // DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
      // 定义一个变量存放最终的消息形式
      String msg = null ;
      if(flag == 1){
         /** 读取所有在线人数发给所有客户端去更新自己的在线人数列表 */
         /** onlineNames = [波仔,zhangsan,波妞]*/
         StringBuilder rs = new StringBuilder();
         Collection<String> onlineNames = ServerChat.onLineSockets.values();
         // 判断是否存在在线人数
         if(onlineNames != null && onlineNames.size() > 0){
            for(String name : onlineNames){
               rs.append(name+ ",");
            }
            // 波仔003197♣♣㏘♣④④♣zhangsan003197♣♣㏘♣④④♣波妞003197♣♣㏘♣④④♣
            // 去掉最后的一个分隔符
            msg = rs.substring(0, rs.lastIndexOf(","));

            /** 将消息发送给所有的客户端 */
            sendMsgToAll(flag,msg);
         }
      }else if(flag == 2 || flag == 3){

      }
   }


   private void sendMsgToAll(int flag, String msg) throws Exception {
      // 拿到所有的在线socket管道 给这些管道写出消息
      Set<Socket> allOnLineSockets = ServerChat.onLineSockets.keySet();
      for(Socket sk :  allOnLineSockets){
         DataOutputStream dos = new DataOutputStream(sk.getOutputStream());
         dos.writeInt(flag); // 消息类型
         dos.writeUTF(msg);
         dos.flush();
      }
   }
}