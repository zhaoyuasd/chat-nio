package com.laozhao.common;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

public class CommonUtil {
    private static final String UTF_8 = "utf-8";

    public static final String END = "end";

    public static String decode(ByteBuffer byteBuffer) {

        byteBuffer.flip();
        /**
         byte[] b=byteBuffer.array();
         int back=0;
         String result=null;
         for(back=0;back<b.length;back++){
         try {
         result=new String(b,0,b.length-back,UTF_8);
         break;
         }catch (Exception e){
         continue;
         }
         }
         byteBuffer.clear();
         if(back!=0){
         for(int i=b.length-back;i<b.length;i++){
         byteBuffer.put(b[i]);
         }
         }
         **/
        String result=null;
        int position;
        int limit;
        //limit = byteBuffer.limit();
        int back = 0;
        while (true) {
            try {
                /***
                Charset charset = Charset.forName(UTF_8);
                CharsetDecoder decoder = charset.newDecoder();
                CharBuffer charBuffer = decoder.decode(byteBuffer);
                result= charBuffer.toString();
                 **/
                byte[] b=byteBuffer.array();
                result=new String(b,byteBuffer.position(),byteBuffer.limit()).trim();
                break;
            } catch (Exception e) {
                System.out.println("get error "+e.getMessage());
                break;
                //back++;
               // byteBuffer.limit(limit-back);
            }
        }
        /**
        if(back!=0){
            byte[]  b=new byte[back];
            for(int i=1;i<=back;i++){
                //b[i]=byteBuffer.get(limit-back+i);
            }
            //byteBuffer.clear();
            //byteBuffer.put(b);
        }
         **/
        return  result;
    }

    /**
     * 有时候读取的字节码 会不全 这个时候后要把全的那部分读出来 不全的那部分继续保持在buf里面
     *
     * @param byteBuffer
     * @param i
     */
    private static String dealByte(ByteBuffer byteBuffer, int i) {
        byteBuffer.limit(byteBuffer.limit() - 1);
        return decode(byteBuffer);
    }

    public static byte[] getByte(String str) {
        if (str == null) {
            throw new RuntimeException(" str must not null");
        }
        try {
            return str.getBytes();
        } catch (Exception e) {
            e.printStackTrace();
        }
        throw new RuntimeException(" str can not covert to utf8");
    }

    public static byte[] endByte() {
        return getByte(END);
    }
}
