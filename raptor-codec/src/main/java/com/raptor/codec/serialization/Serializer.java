package com.raptor.codec.serialization;


public interface Serializer {
	<T> byte[] serialize(T obj);

	<T> T deserialize(byte[] data, Class<T> cls);
}
