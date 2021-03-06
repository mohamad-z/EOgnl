/*
 * Decompiled with CFR 0.139.
 */
package eognl.internal;

import eognl.ClassCacheInspector;
import eognl.internal.ClassCache;
import eognl.internal.HashMapCache;
import eognl.internal.entry.CacheEntryFactory;

public class HashMapClassCache<T>
extends HashMapCache<Class<?>, T>
implements ClassCache<T> {
    private ClassCacheInspector inspector;

    public HashMapClassCache(CacheEntryFactory<Class<?>, T> entryFactory) {
        super(entryFactory);
    }

    @Override
    public void setClassInspector(ClassCacheInspector inspector) {
        this.inspector = inspector;
    }

    @Override
    public T put(Class<?> key, T value) {
        if (this.inspector != null && !this.inspector.shouldCache(key)) {
            return value;
        }
        return super.put(key, value);
    }
}

