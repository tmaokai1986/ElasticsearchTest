package xyz.lockon;

import org.elasticsearch.cluster.metadata.IndexMetadata;
import org.elasticsearch.cluster.routing.Murmur3HashFunction;

public class HashMain {
    public static void main(String[] args) {
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 100; j++) {
                int hashId = calculateScaledShardId(String.valueOf(j), 0);
                if (hashId == i) {
                    System.out.println(String.format("%s, %s", hashId, j));
                    break;
                }
            }
        }

    }

    private static int calculateScaledShardId(String effectiveRouting, int partitionOffset) {
        final int hash = Murmur3HashFunction.hash(effectiveRouting) + partitionOffset;

        // we don't use IMD#getNumberOfShards since the index might have been shrunk such that we need to use the size
        // of original index to hash documents
        return Math.floorMod(hash, 640) / 128;
    }
}
