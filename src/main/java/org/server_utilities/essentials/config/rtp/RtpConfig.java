package org.server_utilities.essentials.config.rtp;

import net.minecraft.core.SectionPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.biome.Biomes;

import java.util.Random;

public class RtpConfig {

    public int defaultRtps = 3;

    public int centerX = 0;
    public int centerZ = 0;
    public int minRadius = 5000;
    public int maxRadius = 10000;

    public Shape shape = Shape.RECTANGLE;

    public ResourceLocation[] blacklistedBiomes = {
            // Oceans
            Biomes.DEEP_FROZEN_OCEAN.location(),
            Biomes.DEEP_COLD_OCEAN.location(),
            Biomes.DEEP_LUKEWARM_OCEAN.location(),
            Biomes.DEEP_OCEAN.location(),
            Biomes.FROZEN_OCEAN.location(),
            Biomes.OCEAN.location(),
            Biomes.COLD_OCEAN.location(),
            Biomes.LUKEWARM_OCEAN.location(),
            Biomes.WARM_OCEAN.location(),
            Biomes.RIVER.location(),
            // End (void)
            Biomes.THE_VOID.location(),
            Biomes.SMALL_END_ISLANDS.location(),
            Biomes.END_BARRENS.location(),
    };

    public enum Shape {
        CIRCLE, RECTANGLE;

        public ChunkPos generateLocation(int centerX, int centerZ, int minRadius, int maxRadius) {
            Random random = new Random();
            ChunkPos chunkPos;
            switch (this) {
                case CIRCLE -> {
                    double radian = random.nextDouble(2 * Math.PI);
                    double x = Math.cos(radian);
                    x *= random.nextInt(minRadius, maxRadius + 1);
                    x += centerX;
                    double z = Math.sin(radian);
                    z *= random.nextInt(minRadius, maxRadius + 1);
                    z += centerZ;
                    chunkPos = new ChunkPos(SectionPos.blockToSectionCoord(x), SectionPos.blockToSectionCoord(z));
                }
                case RECTANGLE -> {
                    chunkPos = new ChunkPos(
                            SectionPos.blockToSectionCoord(generateRangeWithCenter(random, centerX, minRadius, maxRadius)),
                            SectionPos.blockToSectionCoord(generateRangeWithCenter(random, centerZ, minRadius, maxRadius))
                    );
                }
                default -> throw new AssertionError();
            }
            return chunkPos;
        }

        public static int generateRangeWithCenter(Random random, int center, int min, int max) {
            int range = random.nextInt(min, max + 1);
            if (random.nextBoolean()) range *= -1;
            return range + center;
        }
    }

}
