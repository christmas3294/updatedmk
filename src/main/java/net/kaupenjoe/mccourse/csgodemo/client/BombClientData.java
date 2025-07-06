package net.kaupenjoe.mccourse.csgodemo.client;

import net.kaupenjoe.mccourse.csgodemo.team.BombTeam;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BombClientData {
    //维护每个玩家的属于哪个团队数据
    public static boolean bombPlanted = false;
    public static final Map<UUID, BombTeam> players = new HashMap<>();
}