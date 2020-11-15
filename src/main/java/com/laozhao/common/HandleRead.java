package com.laozhao.common;

import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public class HandleRead {

    private static BlockingQueue<byte[]> queue=new ArrayBlockingQueue(200);

    private static volatile boolean shouldGoon=true;

    public  static void handleRead(SelectionKey key) {
        SocketChannel socketChannel = (SocketChannel) key.channel();
        ByteBuffer byteBuffer =ByteBuffer.allocate(1024);
        startWork();
        try {
        while (shouldGoon) {
            int length = socketChannel.read(byteBuffer);
            if (length<=0){
                continue;
            }
            byte[] b = new byte[length];
            System.out.println("add length :"+length);
            byteBuffer.get(b);
            String infos=new String(b);
            System.out.println(infos);
            queue.add(b);
            byteBuffer.compact();
        }
        byte[] b=CommonUtil.endByte();
        ByteBuffer buf=ByteBuffer.allocate(b.length);
        buf.put(b);
        buf.flip();
        key=socketChannel.register(key.selector(), SelectionKey.OP_WRITE);
        } catch (Exception e) {
                e.printStackTrace();
        }

    }

    private static void startWork() {
        new Thread(new Runnable() {
            @Override
            public void run() {
            final ByteBuffer byteBuffer =ByteBuffer.allocate(1024);
            while (shouldGoon) {
                try {
                    int length;
                    boolean goon=true;
                    /** 读取头部整数后 有没有将后续的字符串一并读出来 没有就是false**/
                    boolean currentDeal=true;
                    int infoLength=0;
                    while (goon) {
                        byte[] b=queue.poll(500, TimeUnit.MILLISECONDS);
                        if (b==null){
                            continue;
                        }
                        infoLength=b.length;
                        byteBuffer.put(b);
                        byteBuffer.flip();
                        if(!currentDeal&&infoLength>0){
                            if(byteBuffer.remaining()>infoLength) {
                                byte[] info = new byte[infoLength];
                                byteBuffer.get(info);
                                System.out.println("unDeal:" + new String(info));
                                currentDeal = true;
                            }else {
                                System.out.println("!currentDeal&&infoLength>0 continue");
                                continue;
                            }
                        }

                        // 1. 首先读取头部整数
                        if (byteBuffer.remaining() >= 4) {
                            infoLength = byteBuffer.getInt();
                            currentDeal=false;
                        } else {
                            System.out.println("首先读取头部整数 position:"+byteBuffer.position()+" limit:"+byteBuffer.limit());
                            Thread.sleep(1*1000);
                            continue;

                        }
                        System.out.println("infoLength:" + infoLength);

                        //2. 进入循环 每次验证剩余的是否够一个包 不够则进行读取
                        while (byteBuffer.remaining() >= infoLength) {
                            byte[] info = new byte[infoLength];
                            byteBuffer.get(info);
                            String infoStr = new String(info);
                            System.out.println(infoStr);
                            //  标记处理完毕
                            currentDeal=true;
                            // 判断是否发送过来结束符
                            if (infoStr.equalsIgnoreCase(CommonUtil.END)) {
                                System.out.println(" receive over go out");
                                shouldGoon=false;
                                break;
                            }
                            if (byteBuffer.remaining() >= 4) {
                                infoLength = byteBuffer.getInt();
                                // 标记未处理完毕 进入下一轮循环
                                currentDeal=false;
                            } else {
                                System.out.println("break");
                                break;
                            }
                        }
                        // 将没有读取的部分 压到头部 继续进行填充
                        byteBuffer.compact();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }}).start();
    }

}
