// automatically generated by the FlatBuffers compiler, do not modify

package org.locationtech.geogig.flatbuffers.generated.v1;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import com.google.flatbuffers.FlatBufferBuilder;
import com.google.flatbuffers.Table;

@SuppressWarnings("unused")
public final class NodeTree extends Table {
  public static NodeTree getRootAsNodeTree(ByteBuffer _bb) { return getRootAsNodeTree(_bb, new NodeTree()); }
  public static NodeTree getRootAsNodeTree(ByteBuffer _bb, NodeTree obj) { _bb.order(ByteOrder.LITTLE_ENDIAN); return (obj.__assign(_bb.getInt(_bb.position()) + _bb.position(), _bb)); }
  public void __init(int _i, ByteBuffer _bb) { bb_pos = _i; bb = _bb; }
  public NodeTree __assign(int _i, ByteBuffer _bb) { __init(_i, _bb); return this; }

  public long size() { int o = __offset(4); return o != 0 ? bb.getLong(o + bb_pos) : 0L; }
  public int numTreesRecursive() { int o = __offset(6); return o != 0 ? bb.getInt(o + bb_pos) : 0; }
  public Bucket buckets(int j) { return buckets(new Bucket(), j); }
  public Bucket buckets(Bucket obj, int j) { int o = __offset(8); return o != 0 ? obj.__assign(__indirect(__vector(o) + j * 4), bb) : null; }
  public int bucketsLength() { int o = __offset(8); return o != 0 ? __vector_len(o) : 0; }

  public static int createNodeTree(FlatBufferBuilder builder,
      long size,
      int num_trees_recursive,
      int bucketsOffset) {
    builder.startObject(3);
    NodeTree.addSize(builder, size);
    NodeTree.addBuckets(builder, bucketsOffset);
    NodeTree.addNumTreesRecursive(builder, num_trees_recursive);
    return NodeTree.endNodeTree(builder);
  }

  public static void startNodeTree(FlatBufferBuilder builder) { builder.startObject(3); }
  public static void addSize(FlatBufferBuilder builder, long size) { builder.addLong(0, size, 0L); }
  public static void addNumTreesRecursive(FlatBufferBuilder builder, int numTreesRecursive) { builder.addInt(1, numTreesRecursive, 0); }
  public static void addBuckets(FlatBufferBuilder builder, int bucketsOffset) { builder.addOffset(2, bucketsOffset, 0); }
  public static int createBucketsVector(FlatBufferBuilder builder, int[] data) { builder.startVector(4, data.length, 4); for (int i = data.length - 1; i >= 0; i--) builder.addOffset(data[i]); return builder.endVector(); }
  public static void startBucketsVector(FlatBufferBuilder builder, int numElems) { builder.startVector(4, numElems, 4); }
  public static int endNodeTree(FlatBufferBuilder builder) {
    int o = builder.endObject();
    return o;
  }
}

