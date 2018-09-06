package com.raptor.codec.coder;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.AllArgsConstructor;
import com.raptor.codec.serialization.Serializer;

/**
 * @author hongbin
 * Created on 21/10/2017
 */
public class RPCEncoder extends MessageToByteEncoder {

	private Class<?> genericClass;
	private Serializer serializer;

	public RPCEncoder(){}

	public RPCEncoder(Class<?> genericClass,Serializer serializer){
		this.genericClass = genericClass;
		this.serializer = serializer;
	}


	@Override
	public void encode(ChannelHandlerContext ctx, Object in, ByteBuf out) throws Exception {
		if (genericClass.isInstance(in)) {
			byte[] data = serializer.serialize(in);
			out.writeInt(data.length);
			out.writeBytes(data);
		}
	}
}
