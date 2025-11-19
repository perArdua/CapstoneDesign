package com.example.campusin.support;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * H2에서 MySQL GET_LOCK/RELEASE_LOCK을 흉내 내기 위한 함수.
 */
public final class H2LockFunctions {

    private static final Map<String, Boolean> locks = new ConcurrentHashMap<>();

    private H2LockFunctions() {
    }

    public static int getLock(String name, int timeout) {
        locks.put(name, Boolean.TRUE);
        return 1; // 항상 성공으로 반환
    }

    public static int releaseLock(String name) {
        return locks.remove(name) != null ? 1 : 0;
    }
}
