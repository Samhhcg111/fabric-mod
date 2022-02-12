package net.fabricmc.playtimeInfo;

public interface AFKPlayer {
    boolean isAfk();
    void setAfk(boolean isAfk);

    long getPlaytime();
    void setPlaytime(long playtime);

    long getTempPlaytime();
    void setTempPlaytime(long playtime);

    long getStrictLastActionTime();
    void setStrictLastActionTime(long time);
}
