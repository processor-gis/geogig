// automatically generated by the FlatBuffers compiler, do not modify

package org.locationtech.geogig.flatbuffers.generated.values;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import com.google.flatbuffers.FlatBufferBuilder;
import com.google.flatbuffers.Table;

@SuppressWarnings("unused")
public final class FLOAT_ARRAY extends Table {
  public static FLOAT_ARRAY getRootAsFLOAT_ARRAY(ByteBuffer _bb) { return getRootAsFLOAT_ARRAY(_bb, new FLOAT_ARRAY()); }
  public static FLOAT_ARRAY getRootAsFLOAT_ARRAY(ByteBuffer _bb, FLOAT_ARRAY obj) { _bb.order(ByteOrder.LITTLE_ENDIAN); return (obj.__assign(_bb.getInt(_bb.position()) + _bb.position(), _bb)); }
  public void __init(int _i, ByteBuffer _bb) { bb_pos = _i; bb = _bb; }
  public FLOAT_ARRAY __assign(int _i, ByteBuffer _bb) { __init(_i, _bb); return this; }

  public float value(int j) { int o = __offset(4); return o != 0 ? bb.getFloat(__vector(o) + j * 4) : 0; }
  public int valueLength() { int o = __offset(4); return o != 0 ? __vector_len(o) : 0; }
  public ByteBuffer valueAsByteBuffer() { return __vector_as_bytebuffer(4, 4); }
  public ByteBuffer valueInByteBuffer(ByteBuffer _bb) { return __vector_in_bytebuffer(_bb, 4, 4); }

  public static int createFLOAT_ARRAY(FlatBufferBuilder builder,
      int valueOffset) {
    builder.startObject(1);
    FLOAT_ARRAY.addValue(builder, valueOffset);
    return FLOAT_ARRAY.endFLOAT_ARRAY(builder);
  }

  public static void startFLOAT_ARRAY(FlatBufferBuilder builder) { builder.startObject(1); }
  public static void addValue(FlatBufferBuilder builder, int valueOffset) { builder.addOffset(0, valueOffset, 0); }
  public static int createValueVector(FlatBufferBuilder builder, float[] data) { builder.startVector(4, data.length, 4); for (int i = data.length - 1; i >= 0; i--) builder.addFloat(data[i]); return builder.endVector(); }
  public static void startValueVector(FlatBufferBuilder builder, int numElems) { builder.startVector(4, numElems, 4); }
  public static int endFLOAT_ARRAY(FlatBufferBuilder builder) {
    int o = builder.endObject();
    return o;
  }
}
