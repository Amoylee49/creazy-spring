package com.shorturl.filter;

import com.google.common.base.Preconditions;
import com.google.common.hash.Funnel;
import com.google.common.hash.Hashing;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BloomFilterHelper<T> {

    private int numHashFunctions;
    private int bitSize;
    private Funnel<T> funnel;

//    Funnel<String> ,1000000, 0.01);
    public BloomFilterHelper(Funnel<T> funnel, int expectInsert, double fpp) {
        Preconditions.checkArgument(funnel!= null, "funnel不能为空");
        this.funnel = funnel;
        //计算bit数组长度
        bitSize = optimalNumOfBits(expectInsert,fpp);
        //计算hash方法执行次数
        numHashFunctions = optimalNumOfHashFun(expectInsert,bitSize);
    }

    public int[] murmurHashOffset(T value) {
        int[] offset = new int[numHashFunctions];

        long hash64 = Hashing.murmur3_128().hashObject(value, funnel).asLong();
        int hash1 = (int) hash64;
        int hash2 = (int) (hash64 >>> 32);
        for (int i = 1; i <= numHashFunctions; i++) {
            int nextHash = hash1 + i * hash2;
            if (nextHash < 0) {
                nextHash = ~nextHash;
            }
            offset[i - 1] = nextHash % bitSize;
        }

        return offset;
    }

    /**
     * 计算bit数组长度
     */
    private int optimalNumOfBits(long n, double p) {
        if (p == 0) {
            // 设定最小期望长度
            p = Double.MIN_VALUE;
        }
        int sizeOfBitArray = (int) (-n * Math.log(p) / (Math.log(2) * Math.log(2)));
        return sizeOfBitArray;
    }

    /**
     * 计算hash方法执行次数
     */
    private int optimalNumOfHashFun(long n, long m) {
        int countOfHash = Math.max(1, (int) Math.round((double) m / n * Math.log(2)));
        return countOfHash;
    }
}
