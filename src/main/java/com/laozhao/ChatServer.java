package com.laozhao;

import com.laozhao.common.CommonUtil;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Set;

public class ChatServer {
    public static void main(String[] args) {
        Selector selector=null;
        ServerSocketChannel socketChannel=null;
        try {
         selector=Selector.open();
         socketChannel=ServerSocketChannel.open();
         socketChannel.socket().bind(new InetSocketAddress(9990));
         socketChannel.configureBlocking(false);
         socketChannel.register(selector, SelectionKey.OP_ACCEPT);
         while(true){
            if(selector.select(500)==0){
                continue;
            }
            Iterator<SelectionKey> it=selector.selectedKeys().iterator();
            while (it.hasNext()){
                SelectionKey key=it.next();
                if(!key.isValid()){
                    System.out.println(System.nanoTime()+"   close");
                }
                if(key.isAcceptable()){
                    System.out.println("isAcceptable");
                    handleAccept(key);
                }
                if(key.isWritable()){
                    System.out.println("isWritable");
                    handleWrite(key);
                    key.interestOps(key.interestOps()&~SelectionKey.OP_WRITE);
                    key.selector().wakeup();
                }
                if(key.isReadable()){
                    System.out.println(System.nanoTime()+"   isReadable");
                    handleRead(key);
                }
            }
            selector.selectedKeys().clear();
         }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private static void handleWrite(SelectionKey key) {
        SocketChannel socketChannel= (SocketChannel) key.channel();
        ByteBuffer byteBuffer= (ByteBuffer) key.attachment();
        if(byteBuffer==null){
            byteBuffer=ByteBuffer.allocate(1024);
            byteBuffer.put(CommonUtil.getByte("asd华盛顿"));
            byteBuffer.flip();
        }else {
            System.out.println("not null");
        }

        while (byteBuffer.hasRemaining()){
            try {
                socketChannel.write(byteBuffer);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private static void handleRead(SelectionKey key) {
        SocketChannel socketChannel = (SocketChannel) key.channel();
        ByteBuffer  or= (ByteBuffer) key.attachment();
        System.out.println("or:"+CommonUtil.decode(or));
        ByteBuffer byteBuffer =ByteBuffer.allocate(1024);
        try {
            int length;
            while(true) {
                length=socketChannel.read(byteBuffer);
                if(length==0){
                  //  System.out.println("continue");
                     continue;
                }
                if(length==-1){
                    System.out.println("channel have close go out");
                    break;
                }
                String str=CommonUtil.decode(byteBuffer);
                System.out.println("receive length :"+length+" info:"+str);
                if(str!=null) {
                    String end = str.substring(str.length() - 3);
                    System.out.println("end:" + end);

                    if (end.equalsIgnoreCase(CommonUtil.END)) {
                        System.out.println(" receive over go out");
                        break;
                    }
                }

                byteBuffer.clear();
                byteBuffer.put(CommonUtil.getByte("q"));
            }
            byte[] b=CommonUtil.endByte();
            ByteBuffer buf=ByteBuffer.allocate(b.length);
            buf.put(b);
            buf.flip();
            key=socketChannel.register(key.selector(), SelectionKey.OP_WRITE);
            key.attach(buf);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private static void handleAccept(SelectionKey key) {
        ServerSocketChannel serverSocketChannel= (ServerSocketChannel) key.channel();
        SocketChannel sc= null;
        try {
            sc = serverSocketChannel.accept();
            System.out.println(sc==null);
            if(sc!=null){
                sc.configureBlocking(false);
                sc.register(key.selector(),SelectionKey.OP_READ, ByteBuffer.allocate(1024));
                byte[] m="a lo ha==".getBytes();
                ByteBuffer buf=ByteBuffer.allocate(m.length);
                sc.write(buf);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
