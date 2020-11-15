package com.laozhao.common;

import lombok.Data;
import java.nio.ByteBuffer;

@Data
public class BufferMessage {
    private   ByteBuffer byteBuffer ;

    /** 本次 指定的 infoLength 个数的字节数是否全部读取 **/
    private   boolean currentDeal;
    /** 下一步要从 byteBuffer 读取的字节数的个数 **/
    private   int  infoLength;

  private BufferMessage(){}
  public static BufferMessage init(){
      BufferMessage instance=new BufferMessage();
      instance.setByteBuffer(ByteBuffer.allocate(1024));
      instance.setCurrentDeal(true);
      instance.setInfoLength(0);
      return instance;
  }
}
