package com.laozhao;

import com.laozhao.common.CommonUtil;


import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;

public class ChatClient {
    static  int step=0;
    public static void main(String[] args) {
        ByteBuffer buffer=ByteBuffer.allocate(1024);
        SocketChannel clientSocketChannel=null;
        Selector selector=null;

        try{
           selector=Selector.open();
           clientSocketChannel=SocketChannel.open();
           clientSocketChannel.configureBlocking(false);
           clientSocketChannel.connect(new InetSocketAddress("localhost",19999));
            while (!clientSocketChannel.finishConnect()){

            }
            System.out.println(clientSocketChannel.isConnected());
            /*if(clientSocketChannel.isConnected()){
                System.out.println(" write data ");
                doWrite(clientSocketChannel);

            }*/
            clientSocketChannel.register(selector,SelectionKey.OP_WRITE);
            StringBuffer sb=new StringBuffer();
            System.out.println("等待回复结果");
            boolean goon=true;
            while (goon) {
                if(selector.select()<=0){
                    continue;
                }
                Set<SelectionKey> it = selector.selectedKeys();

                for (SelectionKey key : it) {
                    if(key.isWritable()){
                        System.out.println("isWritable");
                        handleWrite(key);
                        key.interestOps(key.interestOps()&~SelectionKey.OP_WRITE);
                        // key.selector().wakeup();
                    }

                    if(key.isReadable()){
                        System.out.println("isReadable");
                        handleRead(key);
                        // HandleRead.handleRead(key);
                    }

                }
                selector.selectedKeys().clear();
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private static void handleRead(SelectionKey key) {
        ByteBuffer buf = ByteBuffer.allocate(1024);
        SocketChannel sc = (SocketChannel) key.channel();
        try {
            StringBuffer sb=new StringBuffer();
            if (sc.read(buf) > 0) {
                sb.append(CommonUtil.decode(buf));
                System.out.println(sb.toString().trim());
            }
            if(CommonUtil.END.equalsIgnoreCase(sb.toString().trim())){
                System.out.println(" send success");
                sc.close();
                key.selector().close();
            }else {
                key.channel().register(key.selector(),SelectionKey.OP_READ);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     *  这里我们拆包发送 我们消息结构式 int number， string info
     *  其中 number放在首位 占4个字节  后面跟number个字节用来放 info
     *
     *  这里我们分两次发送一个 文本 第一次发送 一部分 之后sleep 200ms  然后发送第二段
     *
     * @param key
     */
    private static void handleWrite(SelectionKey key) {
        SocketChannel socketChannel = (SocketChannel) key.channel();
        String info="hello this just my test for learn new io ";
        String info2=" haha hello word";
        byte[]  m1 = CommonUtil.getByte(info);
        byte[]  m2=CommonUtil.getByte(info2);


        ByteBuffer buf=ByteBuffer.allocate(m1.length+4);
        /***整数  + 字符串字节数  ***/
        buf.putInt(m1.length+m2.length);
        buf.put(m1);
        buf.flip();

        try {
                socketChannel.write(buf);
                System.out.println("send step info :"+ m1.length);


                Thread.sleep(200);
                ByteBuffer buf2 = ByteBuffer.allocate(m2.length);
                buf2.put(m2);
                buf2.flip();
                socketChannel.write(buf2);
                System.out.println("send step info2 :"+ m2.length);


               byte[] b=CommonUtil.endByte();
                ByteBuffer bb=ByteBuffer.allocate(b.length+4);
                bb.putInt(b.length);
                bb.put(b);
                bb.flip();
                socketChannel.write(bb);
                System.out.println(" send over");
              key.channel().register(key.selector(),SelectionKey.OP_READ);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private static void doWrite(SocketChannel socketChannel) {
        byte[] m;
        try {

            for(int i=1;i<=1;i++){
                String info="hello this just my test for learn new io ";
                String info2=" haha hello word";
                byte[] b2=CommonUtil.getByte(info2);
                info=i+":"+info;
                m = CommonUtil.getByte(info);
                ByteBuffer buf=ByteBuffer.allocate(m.length+4);
               /***整数  + 字符串字节数  ***/
                buf.putInt(m.length+b2.length);
                buf.put(m);
                buf.flip();
                System.out.println("send time :"+i);
                socketChannel.write(buf);
                Thread.sleep(200);
                ByteBuffer buf2=ByteBuffer.allocate(b2.length);
                buf2.put(b2);
                socketChannel.write(buf2);
            }
            byte[] b=CommonUtil.endByte();
            ByteBuffer bb=ByteBuffer.allocate(b.length+4);
            bb.putInt(b.length);
            bb.put(b);
            bb.flip();
            socketChannel.write(bb);
            System.out.println(" send over");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
