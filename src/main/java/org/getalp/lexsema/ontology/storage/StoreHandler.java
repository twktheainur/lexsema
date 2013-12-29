package org.getalp.lexsema.ontology.storage;

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
