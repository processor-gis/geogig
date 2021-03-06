// automatically generated by the FlatBuffers compiler, do not modify

package org.locationtech.geogig.flatbuffers.generated.v1.values;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import com.google.flatbuffers.FlatBufferBuilder;
import com.google.flatbuffers.Table;

@SuppressWarnings("unused")
public final class Value extends Table {
  public static Value getRootAsValue(ByteBuffer _bb) { return getRootAsValue(_bb, new Value()); }
  public static Value getRootAsValue(ByteBuffer _bb, Value obj) { _bb.order(ByteOrder.LITTLE_ENDIAN); return (obj.__assign(_bb.getInt(_bb.position()) + _bb.position(), _bb)); }
  public void __init(int _i, ByteBuffer _bb) { bb_pos = _i; bb = _bb; }
  public Value __assign(int _i, ByteBuffer _bb) { __init(_i, _bb); return this; }

  public byte valueType() { int o = __offset(4); return o != 0 ? bb.get(o + bb_pos) : 0; }
  public Table value(Table obj) { int o = __offset(6); return o != 0 ? __union(obj, o) : null; }

  public static int createValue(FlatBufferBuilder builder,
      byte value_type,
      int valueOffset) {
    builder.startObject(2);
    Value.addValue(builder, valueOffset);
    Value.addValueType(builder, value_type);
    return Value.endValue(builder);
  }

  public static void startValue(FlatBufferBuilder builder) { builder.startObject(2); }
  public static void addValueType(FlatBufferBuilder builder, byte valueType) { builder.addByte(0, valueType, 0); }
  public static void addValue(FlatBufferBuilder builder, int valueOffset) { builder.addOffset(1, valueOffset, 0); }
  public static int endValue(FlatBufferBuilder builder) {
    int o = builder.endObject();
    return o;
  }
}

