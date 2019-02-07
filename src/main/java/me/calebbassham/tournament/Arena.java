package me.calebbassham.tournament;

import org.bukkit.Location;

public class Arena {

    private final Location spawn1;
    private final Location spawn2;
    private boolean available = true;

    public Arena(final Location spawn1, final Location spawn2) {
        this.spawn1 = spawn1;
        this.spawn2 = spawn2;
    }

    public final Location getSpawn1() {
        return spawn1;
    }

    public final Location getSpawn2() {
        return spawn2;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }
}
