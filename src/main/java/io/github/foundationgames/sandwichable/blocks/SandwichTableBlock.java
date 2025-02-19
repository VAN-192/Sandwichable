package io.github.foundationgames.sandwichable.blocks;

import io.github.foundationgames.sandwichable.blocks.entity.SandwichTableBlockEntity;
import io.github.foundationgames.sandwichable.util.Util;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

public class SandwichTableBlock extends Block implements BlockEntityProvider, SneakInteractable {
    public SandwichTableBlock(Settings settings) {
        super(settings);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new SandwichTableBlockEntity(pos, state);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (!world.isClient() && blockEntity instanceof SandwichTableBlockEntity) {
            SandwichTableBlockEntity sBlockEntity = (SandwichTableBlockEntity)world.getBlockEntity(pos);
            sBlockEntity.getSandwich().interact(world, new Vec3d(pos.getX()+0.5, pos.getY(), pos.getZ()+0.5), player, hand, player.isSneaking());
            Util.sync(sBlockEntity);
        }
        return ActionResult.success(world.isClient());
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        super.scheduledTick(state, world, pos, random);
        ejectSandwich(world, pos);
    }

    public void ejectSandwich(World world, BlockPos pos) {
        BlockEntity be = world.getBlockEntity(pos);
        if(be instanceof SandwichTableBlockEntity) {
            SandwichTableBlockEntity blockEntity = (SandwichTableBlockEntity)be;
            blockEntity.getSandwich().ejectSandwich(world, new Vec3d(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5));
            Util.sync(blockEntity);
        }
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (state.getBlock() != newState.getBlock()) {
            BlockEntity be = world.getBlockEntity(pos);
            if (be instanceof SandwichTableBlockEntity) {
                SandwichTableBlockEntity blockEntity = (SandwichTableBlockEntity)world.getBlockEntity(pos);
                blockEntity.getSandwich().ejectSandwich(world, new Vec3d(pos.getX()+0.5, pos.getY(), pos.getZ()+0.5));
                world.updateNeighbors(pos, this);
            }
            super.onStateReplaced(state, world, pos, newState, moved);
        }
    }
}
