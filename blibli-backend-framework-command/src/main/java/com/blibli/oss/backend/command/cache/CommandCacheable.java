package com.blibli.oss.backend.command.cache;

import java.util.Collection;

public interface CommandCacheable<R, T> {

  default String cacheKey(R request) {
    return null;
  }

  default Collection<String> evictKeys(R request) {
    return null;
  }

  default Class<T> responseClass() {
    throw new UnsupportedOperationException("No response class available.");
  }
}
