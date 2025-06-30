package net.kaupenjoe.mccourse.talent;

import net.kaupenjoe.mccourse.MCCourseMod;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

import java.util.UUID;

/**
 * Basic talent enum for the RPG style talent tree.
 */
public enum Talent {
    STRENGTH(Attributes.ATTACK_DAMAGE,
            UUID.fromString("c31b7f2b-3ad2-4f46-8c4f-57b7c01d3a50"),
            2.0,
            AttributeModifier.Operation.ADDITION,
            20,
            20,new ResourceLocation(MCCourseMod.MOD_ID,
           "textures/block/alexandrite_block.png"),"技能A",0),
    SPEED(Attributes.MOVEMENT_SPEED,
            UUID.fromString("0e77f33a-28d2-4aba-a820-527b65f3d470"),
            0.1,
            AttributeModifier.Operation.MULTIPLY_TOTAL,
            80,
            20,new ResourceLocation(MCCourseMod.MOD_ID+"textures/block/alexandrite_door_bottom.png"),"技能",0),
    HEALTH(Attributes.MAX_HEALTH,
            UUID.fromString("b2c4d38e-010c-4210-b1dc-9d7358126d21"),
            0,
            AttributeModifier.Operation.ADDITION,
            20,
            40,new ResourceLocation(MCCourseMod.MOD_ID+"textures/block/alexandrite_door_bottom.png"),"技能B",0);

    private final Attribute attribute;
    private final UUID uuid;
    private final double amount;
    private final AttributeModifier.Operation operation;
    private final int guiX;
    private final int guiY;
    public ResourceLocation icon;
    public String name;
    public int count;

    Talent(Attribute attribute, UUID uuid, double amount, AttributeModifier.Operation operation, int guiX, int guiY, ResourceLocation icon,String name,int count) {
        this.attribute = attribute;
        this.uuid = uuid;
        this.amount = amount;
        this.operation = operation;
        this.guiX = guiX;
        this.guiY = guiY;
        this.icon = icon;
        this.name = name;
        this.count = count;
    }

    public AttributeModifier createModifier() {
        return new AttributeModifier(uuid.toString(), amount, operation);
    }

    public Attribute getAttribute() {
        return attribute;
    }

    public int getGuiX() {
        return guiX;
    }

    public int getGuiY() {
        return guiY;
    }


    public ResourceLocation geticon(){
        return this.icon;
    }

    public String getName() {
        return name;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getCount() {
        return count;
    }
}