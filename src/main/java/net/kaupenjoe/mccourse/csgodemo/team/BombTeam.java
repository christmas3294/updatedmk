package net.kaupenjoe.mccourse.csgodemo.team;

public enum BombTeam {
    TERRORISTS(false),  // 默认未死亡
    COUNTER_TERRORISTS(false);  // 默认未死亡


    private  boolean isDead;

    // 构造函数
    BombTeam(boolean isDead) {
        this.isDead = isDead;
    }

    // 获取玩家是否死亡
    public boolean isDead() {
        return isDead;
    }

    // 设置玩家死亡状态
    public void setDead(boolean isDead) {
         this.isDead = isDead;
    }
}