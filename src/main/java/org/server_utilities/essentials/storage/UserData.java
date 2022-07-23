package org.server_utilities.essentials.storage;

import org.server_utilities.essentials.EssentialsMod;
import org.server_utilities.essentials.util.teleportation.Home;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class UserData {

    private final List<Home> homes = new LinkedList<>();
    private int rtpsLeft = EssentialsMod.getConfig().main().rtpConfig.defaultRtps;

    public List<Home> getHomes() {
        return homes;
    }

    public void setRtpsLeft(int rtpsLeft) {
        this.rtpsLeft = rtpsLeft;
    }

    public int getRtpsLeft() {
        return rtpsLeft;
    }

    public Optional<Home> getHome(String name) {
        return homes.stream().filter(home -> home.name().equals(name)).findFirst();
    }

}
