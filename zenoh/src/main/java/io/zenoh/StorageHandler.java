package io.zenoh;


import java.nio.ByteBuffer;

/**
 * A callback interface to be implemented by the implementation of a Storage.
 */
public interface StorageHandler extends DataHandler, QueryHandler {}