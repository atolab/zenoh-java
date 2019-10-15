package io.zenoh;

/**
 * A callback interface to be implemented by the implementation of a Storage.
 */
public interface StorageHandler extends DataHandler, QueryHandler {}