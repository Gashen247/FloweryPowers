package com.flowers.flowerymod;

import net.neoforged.neoforge.common.ModConfigSpec;

public final class Config {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.IntValue WIND_COOLDOWN_SECONDS = BUILDER
            .comment("Cooldown for Mysterious wind! (right click), in seconds.")
            .defineInRange("windCooldownSeconds", 10, 1, 600);

    public static final ModConfigSpec.DoubleValue DASH_SPEED = BUILDER
            .comment("Speed of the Jarona! dash, in blocks per tick.")
            .defineInRange("dashSpeed", 2.2, 0.1, 10.0);

    public static final ModConfigSpec.DoubleValue DASH_DAMAGE = BUILDER
            .comment("Damage dealt by one Jarona! dash.")
            .defineInRange("dashDamage", 6.0, 0.0, 100.0);

    public static final ModConfigSpec.IntValue DASH_DURATION_TICKS = BUILDER
            .comment("How long the dash pushes the player, in ticks (20 ticks = 1 second).")
            .defineInRange("dashDurationTicks", 6, 1, 40);

    public static final ModConfigSpec.DoubleValue WIND_RADIUS = BUILDER
            .comment("Radius of Mysterious wind!, in blocks.")
            .defineInRange("windRadius", 10.0, 1.0, 100.0);

    public static final ModConfigSpec.DoubleValue WIND_KNOCKBACK = BUILDER
            .comment("Base knockback strength of Mysterious wind!.")
            .defineInRange("windKnockback", 2.5, 0.0, 20.0);

    public static final ModConfigSpec.DoubleValue TRAIL_SPACING_BLOCKS = BUILDER
            .comment("Minimum distance between two afterimages, in blocks. Bigger value means fewer ghosts.")
            .defineInRange("trailSpacingBlocks", 0.7, 0.1, 10.0);

    public static final ModConfigSpec.IntValue TRAIL_MAX_AFTERIMAGES = BUILDER
            .comment("Maximum number of live afterimages per player. Prevents trail spam.")
            .defineInRange("trailMaxAfterimages", 24, 1, 200);

    public static final ModConfigSpec.IntValue TRAIL_LIFETIME_TICKS = BUILDER
            .comment("How long one afterimage stays in the world before fading out, in ticks.")
            .defineInRange("trailLifetimeTicks", 40, 1, 200);

    public static final ModConfigSpec.DoubleValue WIND_RUN_SPEED = BUILDER
            .comment("Speed of the W + right click wind run, in blocks per tick.")
            .defineInRange("windRunSpeed", 0.9, 0.1, 5.0);

    public static final ModConfigSpec.IntValue WIND_RUN_REFRESH_TICKS = BUILDER
            .comment("How long one W + right click input keeps the wind run alive, in ticks.")
            .defineInRange("windRunRefreshTicks", 8, 1, 100);

    static final ModConfigSpec SPEC = BUILDER.build();

    private Config() {
    }
}
