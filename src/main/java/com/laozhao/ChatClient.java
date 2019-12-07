package com.laozhao;

import com.laozhao.common.CommonUtil;


import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

import java.util.Set;

public class ChatClient {
    public static void main(String[] args) {
        ByteBuffer buffer=ByteBuffer.allocate(1024);
        SocketChannel socketChannel=null;
        Selector selector=null;
        try{
            selector=Selector.open();
           socketChannel=SocketChannel.open();
           socketChannel.configureBlocking(false);
           socketChannel.connect(new InetSocketAddress("localhost",9990));
            while (!socketChannel.finishConnect()){

            }
            System.out.println(socketChannel.isConnected());
            if(socketChannel.isConnected()){
                System.out.println(" write data ");
                doWrite(socketChannel);
            }
            socketChannel.register(selector,SelectionKey.OP_READ);
            StringBuffer sb=new StringBuffer();
            System.out.println("等待回复结果");
            boolean goon=true;
            while (goon) {
                if(selector.select()<=0){
                    continue;
                }
                Set<SelectionKey> it = selector.selectedKeys();
                ByteBuffer buf = ByteBuffer.allocate(1024);
                for (SelectionKey key : it) {
                    buf.clear();
                    SocketChannel sc = (SocketChannel) key.channel();
                    if (sc.read(buf) > 0) {
                        sb.append(CommonUtil.decode(buf));
                        System.out.println(sb.toString().trim());
                    }
                    if(CommonUtil.END.equalsIgnoreCase(sb.toString().trim())){
                        System.out.println(" send success");
                        sc.close();
                        selector.close();
                        goon=false;
                        break;
                    }
                }
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private static void doWrite(SocketChannel socketChannel) {
        byte[] m;
        try {

            for(int i=1;i<100;i++){
                String info="hello this just my test for learn new io haha hello word";
                info=i+":"+info;
                m = CommonUtil.getByte(info);
                ByteBuffer buf=ByteBuffer.allocate(m.length);
                buf.put(m);
                buf.flip();
                System.out.println("send time :"+i);
                socketChannel.write(buf);
            }
            byte[] b=CommonUtil.endByte();
            ByteBuffer bb=ByteBuffer.allocate(b.length);
            bb.put(b);
            bb.flip();
            socketChannel.write(bb);
            System.out.println(" send over");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
