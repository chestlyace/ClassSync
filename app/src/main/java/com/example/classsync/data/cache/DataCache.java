package com.example.classsync.data.cache;

import androidx.annotation.Nullable;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public final class DataCache {
    private static DataCache instance;

    private final ConcurrentHashMap<String, Object> documentCache = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, List<?>> collectionCache = new ConcurrentHashMap<>();

    private DataCache() {
    }

    public static synchronized DataCache getInstance() {
        if (instance == null) {
            instance = new DataCache();
        }
        return instance;
    }

    public void putDocument(String path, Object data) {
        if (path != null && data != null) {
            documentCache.put(path, data);
        }
    }

    @Nullable
    @SuppressWarnings("unchecked")
    public <T> T getDocument(String path) {
        return (T) documentCache.get(path);
    }

    public void putCollection(String path, List<?> data) {
        if (path != null && data != null) {
            collectionCache.put(path, data);
        }
    }

    @Nullable
    @SuppressWarnings("unchecked")
    public <T> List<T> getCollection(String path) {
        return (List<T>) collectionCache.get(path);
    }

    public void invalidate(String path) {
        documentCache.remove(path);
        collectionCache.remove(path);
    }

    public void invalidatePrefix(String prefix) {
        documentCache.keySet().removeIf(k -> k.startsWith(prefix));
        collectionCache.keySet().removeIf(k -> k.startsWith(prefix));
    }

    public void clear() {
        documentCache.clear();
        collectionCache.clear();
    }
}
