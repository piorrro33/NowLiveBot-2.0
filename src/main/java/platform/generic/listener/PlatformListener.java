/*
 * Copyright 2016-2017 Ague Mort of Veteran Software
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation the
 * rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the
 * Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package platform.generic.listener;

import platform.discord.controller.DiscordController;
import platform.twitch.controller.TwitchController;
import util.DiscordLogger;

import java.time.LocalDateTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author Veteran Software by Ague Mort
 */
public class PlatformListener {
    private static ScheduledExecutorService executor = Executors.newScheduledThreadPool(3);

    public PlatformListener() {
        try {
            executor.scheduleWithFixedDelay(this::run, 0, 90, TimeUnit.SECONDS);
        } catch (Exception e) {
            System.out.println("[~ERROR~] Caught an exception while keeping the executors active");
        }
    }

    private synchronized void run() {
        checkStreams();
        announceStreams();
        editDeleteAnnouncements();
        System.out.println("[SYSTEM] Cycle Complete. Waiting...");
    }

    private synchronized void checkStreams() {
        LocalDateTime timeNow = LocalDateTime.now();
        new DiscordLogger(" :poop: **Checking streams...**", null);
        System.out.println("[SYSTEM] Checking streams... " + timeNow);

        TwitchController twitch = new TwitchController();
        twitch.checkLiveStreams();
    }

    private synchronized void editDeleteAnnouncements() {
        LocalDateTime timeNow = LocalDateTime.now();
        new DiscordLogger(" :poop: **Editing and Deleting Announcements...**", null);
        System.out.println("[SYSTEM] Editing and Deleting Announcements... " + timeNow);

        DiscordController discord = new DiscordController();
        discord.offlineStream();
    }

    private synchronized void announceStreams() {
        LocalDateTime timeNow = LocalDateTime.now();
        new DiscordLogger(" :poop: **Announcing New Streams...**", null);
        System.out.println("[SYSTEM] Announcing New Streams... " + timeNow);

        DiscordController discord = new DiscordController();
        discord.announceChannel("twitch", "channel");
        discord.announceChannel("twitch", "game");
    }
}
