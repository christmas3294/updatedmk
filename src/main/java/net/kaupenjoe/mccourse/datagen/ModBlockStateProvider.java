package net.kaupenjoe.mccourse.datagen;

import net.kaupenjoe.mccourse.MCCourseMod;
import net.kaupenjoe.mccourse.block.ModBlocks;
import net.kaupenjoe.mccourse.block.custom.AlexandriteLampBlock;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.*;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlockStateProvider extends BlockStateProvider {
    public ModBlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, MCCourseMod.MOD_ID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        blockWithItem(ModBlocks.ALEXANDRITE_BLOCK);
        blockWithItem(ModBlocks.RAW_ALEXANDRITE_BLOCK);

        blockWithItem(ModBlocks.ALEXANDRITE_ORE);
        blockWithItem(ModBlocks.DEEPSLATE_ALEXANDRITE_ORE);
        blockWithItem(ModBlocks.END_STONE_ALEXANDRITE_ORE);
        blockWithItem(ModBlocks.NETHER_ALEXANDRITE_ORE);

        blockWithItem(ModBlocks.SOUND_BLOCK);

        stairsBlock((StairBlock) ModBlocks.ALEXANDRITE_STAIRS.get(), blockTexture(ModBlocks.ALEXANDRITE_BLOCK.get()));
        slabBlock(((SlabBlock) ModBlocks.ALEXANDRITE_SLAB.get()), blockTexture(ModBlocks.ALEXANDRITE_BLOCK.get()), blockTexture(ModBlocks.ALEXANDRITE_BLOCK.get()));

        buttonBlock((ButtonBlock) ModBlocks.ALEXANDRITE_BUTTON.get(), blockTexture(ModBlocks.ALEXANDRITE_BLOCK.get()));
        pressurePlateBlock((PressurePlateBlock) ModBlocks.ALEXANDRITE_PRESSURE_PLATE.get(), blockTexture(ModBlocks.ALEXANDRITE_BLOCK.get()));

        fenceBlock((FenceBlock) ModBlocks.ALEXANDRITE_FENCE.get(), blockTexture(ModBlocks.ALEXANDRITE_BLOCK.get()));
        fenceGateBlock((FenceGateBlock) ModBlocks.ALEXANDRITE_FENCE_GATE.get(), blockTexture(ModBlocks.ALEXANDRITE_BLOCK.get()));
        wallBlock((WallBlock) ModBlocks.ALEXANDRITE_WALL.get(), blockTexture(ModBlocks.ALEXANDRITE_BLOCK.get()));

        doorBlockWithRenderType((DoorBlock)ModBlocks.ALEXANDRITE_DOOR.get(), modLoc("block/alexandrite_door_bottom"), modLoc("block/alexandrite_door_top"), "cutout");
        trapdoorBlockWithRenderType((TrapDoorBlock) ModBlocks.ALEXANDRITE_TRAPDOOR.get(), modLoc("block/alexandrite_trapdoor"), true, "cutout");

        blockItem(ModBlocks.ALEXANDRITE_STAIRS);
        blockItem(ModBlocks.ALEXANDRITE_SLAB);
        blockItem(ModBlocks.ALEXANDRITE_PRESSURE_PLATE);
        blockItem(ModBlocks.ALEXANDRITE_FENCE_GATE);
        blockItem(ModBlocks.ALEXANDRITE_TRAPDOOR, "_bottom");

        customLamp();
    }
//用来为 Alexandrite Lamp（亚历山大灯）注册特别的方块状态和模型。
//
//关键点：这个灯有两种状态：亮起（alexandrite_lamp_on）和熄灭（alexandrite_lamp_off）。它使用了 forAllStates 来根据方块的状态（是否被点击）来切换模型。
//
//如果方块的 CLICKED 状态为 true，则使用 亮起的模型。
//
//如果 CLICKED 状态为 false，则使用 熄灭的模型。
    private void customLamp() {
        getVariantBuilder(ModBlocks.ALEXANDRITE_LAMP.get()).forAllStates(state -> {
            if(state.getValue(AlexandriteLampBlock.CLICKED)) {
                return new ConfiguredModel[]{new ConfiguredModel(models().cubeAll("alexandrite_lamp_on",
                        new ResourceLocation(MCCourseMod.MOD_ID, "block/" + "alexandrite_lamp_on")))};
            } else {
                return new ConfiguredModel[]{new ConfiguredModel(models().cubeAll("alexandrite_lamp_off",
                        new ResourceLocation(MCCourseMod.MOD_ID, "block/" +"alexandrite_lamp_off")))};
            }
        });
        simpleBlockItem(ModBlocks.ALEXANDRITE_LAMP.get(), models().cubeAll("alexandrite_lamp_on",
                new ResourceLocation(MCCourseMod.MOD_ID, "block/" +"alexandrite_lamp_on")));
    }
//8.blockItem(RegistryObject<Block> blockRegistryObject, String appendix)
//这个方法是用来为方块注册物品模型，它会基于方块的路径生成模型文件，并允许你加上一些附加信息（例如 _bottom 用于区分不同的模型）。
    private void blockItem(RegistryObject<Block> blockRegistryObject, String appendix) {
        simpleBlockItem(blockRegistryObject.get(), new ModelFile.UncheckedModelFile("mccourse:block/" + ForgeRegistries.BLOCKS.getKey(blockRegistryObject.get()).getPath() + appendix));
    }
//9.blockItem(RegistryObject<Block> blockRegistryObject)
//这个方法也是用来为方块注册物品模型，但它没有附加的 appendix，是直接注册方块的默认模型。
    private void blockItem(RegistryObject<Block> blockRegistryObject) {
        simpleBlockItem(blockRegistryObject.get(), new ModelFile.UncheckedModelFile("mccourse:block/" + ForgeRegistries.BLOCKS.getKey(blockRegistryObject.get()).getPath()));
    }

    private void blockWithItem(RegistryObject<Block> blockRegistryObject) {
        simpleBlockWithItem(blockRegistryObject.get(), cubeAll(blockRegistryObject.get()));
    }
}
