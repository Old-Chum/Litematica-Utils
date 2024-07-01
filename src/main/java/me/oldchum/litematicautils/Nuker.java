package me.oldchum.litematicautils;

import fi.dy.masa.litematica.data.DataManager;
import fi.dy.masa.litematica.schematic.placement.SchematicPlacement;
import fi.dy.masa.litematica.schematic.placement.SchematicPlacementManager;
import fi.dy.masa.litematica.world.SchematicWorldHandler;
import fi.dy.masa.litematica.world.WorldSchematic;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FluidBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

import java.util.*;

/**
 * @author Old Chum
 * @since June 30, 2024
 */
public class Nuker {
    public static final Comparator<BlockPos> BLOCKPOS_DIST_COMPARATOR = Comparator.comparingDouble(o -> o.getSquaredDistance(MinecraftClient.getInstance().player.getX(), MinecraftClient.getInstance().player.getY(), MinecraftClient.getInstance().player.getZ()));

    private static final MinecraftClient mc = MinecraftClient.getInstance();

    private static Map<BlockPos, Long> attemptedBreaks = new HashMap<>();

    private static Timer delayedDestroyTimer = new Timer();
    private static BlockPos delayedDestroyPos = null;

    public static void tickNuker () {
        if (!LitematicaUtils.NUKER_ENABLED.getBooleanValue()) {
            return;
        }

        if (mc.world == null) {
            return;
        }

        ClientPlayerEntity player = mc.player;

        if (player == null) {
            return;
        }

        WorldSchematic worldSchematic = SchematicWorldHandler.getSchematicWorld();

        if (worldSchematic == null) {
            return;
        }

        long hardBlockTimeout = 1000l; // TODO

        if (delayedDestroyPos != null && delayedDestroyTimer.hasPassed(hardBlockTimeout)) {
            delayedDestroyPos = null;
        }

        double nukerRange = 4.5; // TODO
        boolean nukerFlatten = true; // TODO

        Box bb = new Box(
                (int) player.getX() - nukerRange,
                (int) (nukerFlatten ? player.getY() : player.getY() - nukerRange),
                (int) player.getZ() - nukerRange,
                (int) player.getX() + nukerRange,
                (int) player.getY() + nukerRange,
                (int) player.getZ() + nukerRange);

        List<BlockPos> blocks = getClosestBlocksInBox(bb, nukerRange);

        for (BlockPos pos : blocks) {
            BlockState state = mc.world.getBlockState(pos);
            Block block = state.getBlock();

            if (!shouldNuke(pos, worldSchematic)) continue;

            if (block != Blocks.AIR && !(block instanceof FluidBlock)) {
                if (pos.getSquaredDistance(player.getEyePos()) <= nukerRange * nukerRange) {
                    float delta = block.getDefaultState().calcBlockBreakingDelta(player, mc.world, pos);

                    // See: net.minecraft.server.management.ServerPlayerInteractionManager#processBlockBreakingAction()
                    // The server will accept any instant break with a relative hardness >= 0.7.
                    // If the relative hardness is < 0.7, the server will remember the player was trying to break
                    // and make break progress while it lets the player break any insta-break allowed blocks.
                    // We allow configurable thresholds because different servers might have different ACs.

                    double deltaThreshold = 0.7; // TODO

                    if (delta >= deltaThreshold || delayedDestroyPos == null) {
                        if (!attemptedBreaks.containsKey(pos)) {
                            mc.player.networkHandler.sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.START_DESTROY_BLOCK, pos, Direction.NORTH));
                            mc.player.networkHandler.sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.STOP_DESTROY_BLOCK, pos, Direction.NORTH));

                            attemptedBreaks.put(pos, System.currentTimeMillis());

                            if (delta < deltaThreshold) {
                                delayedDestroyPos = pos;
                                delayedDestroyTimer.reset();
                            }
                        }
                    }
                }
            }
        }

        int timeout = 0; // TODO

        List<BlockPos> toRemove = new ArrayList<>();
        for (BlockPos pos : attemptedBreaks.keySet()) {
            if (System.currentTimeMillis() - attemptedBreaks.get(pos) >= timeout) {
                toRemove.add(pos);
            }
        }

        for (BlockPos pos : toRemove) {
            attemptedBreaks.remove(pos);
        }
    }

    // TODO: Only nuke the selected placement?
    private static boolean shouldNuke (BlockPos pos, WorldSchematic worldSchematic) {
        BlockState schemState = worldSchematic.getBlockState(pos);
        BlockState mcState = mc.world.getBlockState(pos);

        // TODO: Add an option to allow players to keep blocks of the same Block but different State
        // TODO: Figure out wtf is going on with schematic placements so that we can actually remove air inside of a schematic. This is retarded.
        return schemState.getBlock() != Blocks.AIR && schemState != mcState;

        // TODO: Once we have an equivalent option
//        if (nukerMode.equals(NukerMode.BLOCKS.name())) {
//            return !isSchemAirBlock(schemState);
//        } else if (nukerMode.equals(NukerMode.AIR.name())) {
//            return isSchemAirBlock(schemState);
//        } else {
//            return true; // Do we not support cave air?
//        }
    }

    /**
     * TODO: Do we actually just wanna use BlockPos.iterateOutwards()? Would be faster but might be worse.
     *       Should at least add it as an option for people who might be using huge a huge nuker range for whatever
     *       reason.
     *
     * @param radius The radius of the sphere. -1 if all blocks in the bb should be included, regardless of if they are
     *               in the sphere.
     * @return A list of {@link BlockPos}' in <code>bb</code> intersecting with the sphere of radius <code>radius</code>
     *         centered on the middle of the player at eye height, sorted by ascending distance to the player.
     */
    public static List<BlockPos> getClosestBlocksInBox (Box bb, double radius) {
        ClientPlayerEntity player = mc.player;
        List<BlockPos> ret = new ArrayList<>();

        for (BlockPos pos : getAllInBB(bb)) {
            if (radius == -1 || pos.getSquaredDistance(player.getEyePos()) <= radius * radius) {
                ret.add(new BlockPos(pos));
            }
        }

        ret.sort(BLOCKPOS_DIST_COMPARATOR);
        return ret;
    }

    public static Iterable<BlockPos> getAllInBB (Box bb) {
        return BlockPos.iterate((int) bb.minX, (int) bb.minY, (int) bb.minZ, (int) bb.maxX, (int) bb.maxY, (int) bb.maxZ);
    }

    public static boolean bbContains (fi.dy.masa.litematica.selection.Box litematicaBox, BlockPos pos) {
        Box bb = new Box(blockPosToVec3d(litematicaBox.getPos1()), blockPosToVec3d(litematicaBox.getPos2()));

        return pos.getX() <= bb.maxX && pos.getX() >= bb.minX &&
                pos.getY() <= bb.maxY && pos.getY() >= bb.minY &&
                pos.getZ() <= bb.maxZ && pos.getZ() >= bb.minZ;
    }

    public static Vec3d blockPosToVec3d (BlockPos pos) {
        return new Vec3d(pos.getX(), pos.getY(), pos.getZ());
    }
}
