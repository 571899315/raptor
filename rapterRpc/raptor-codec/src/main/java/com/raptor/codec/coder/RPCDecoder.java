package com.raptor.codec.coder;

import java.util.List;

import com.raptor.codec.serialization.Serializer;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

/**
 * @author hongbin
 * Created on 21/10/2017
 */
public class RPCDecoder extends ByteToMessageDecoder {

	private Class<?> genericClass;
	private Serializer serializer;


	public RPCDecoder() {
	}

	public RPCDecoder(Class<?> genericClass, Serializer serializer) {
		this.genericClass = genericClass;
		this.serializer = serializer;
	}

	@Override
	public void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
		if (in.readableBytes() < 4) {
			return;
		}
		in.markReaderIndex();
		int dataLength = in.readInt();
		if (in.readableBytes() < dataLength) {
			in.resetReaderIndex();
			return;
		}
		byte[] data = new byte[dataLength];
		in.readBytes(data);
		out.add(serializer.deserialize(data, genericClass));
	}
}
