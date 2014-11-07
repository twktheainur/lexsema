package org.getalp.lexsema.ontolex.graph.storage;

import org.getalp.lexsema.ontolex.graph.store.Store;

/**
 * A static register class for a triple store
 */
public final class StoreHandler {

    private static Store store;

    public static Store getStore() {
        return store;
    }

    public static void registerStoreInstance(Store s) {
        store = s;
    }

    public static void release() {
        store.close();
    }


}
