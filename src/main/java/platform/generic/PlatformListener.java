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

package platform.generic;

import platform.discord.controller.DiscordController;
import util.DiscordLogger;
import util.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author Veteran Software by Ague Mort
 */
public class PlatformListener implements Runnable {
    private static ScheduledExecutorService executor = Executors.newScheduledThreadPool(3);
    private DiscordController discord = new DiscordController();

    public PlatformListener() {
        try {
            executor.scheduleWithFixedDelay(this, 0, 30, TimeUnit.SECONDS);
        } catch (Exception e) {
            System.out.println("[~ERROR~] Caught an exception while keeping the executors active");
        } catch (Throwable t) {
            System.err.println("Uncaught exception is detected! " + t + " st: " + Arrays.toString(t.getStackTrace()));
        }
    }

    @Override
    public synchronized void run() {

        Thread.currentThread().setUncaughtExceptionHandler(new ExceptionHandler());
        editDeleteAnnouncements();
        announceStreams();

        String loggerBuilder;
        if (discord.getAnnounced().length() > 0) {
            loggerBuilder = "```Markdown\n# Streams Announced\n " + discord.getAnnounced().toString() + "```";
            new DiscordLogger(loggerBuilder, null);
            discord.setAnnounced(new StringBuilder());
        }

        if (discord.getEdited().length() > 0) {
            loggerBuilder = "```Markdown\n# Announcements Edited\n" + discord.getEdited().toString() + "```";
            new DiscordLogger(loggerBuilder, null);
            discord.setEdited(new StringBuilder());
        }

        if (discord.getDeleted().length() > 0) {
            loggerBuilder = "```Markdown\n# Announcements Deleted\n" + discord.getDeleted().toString() + "```";
            new DiscordLogger(loggerBuilder, null);
            discord.setDeleted(new StringBuilder());
        }

        if (discord.getPermsRead().length() > 0) {
            loggerBuilder = "```Markdown\n# Permissions Error (Read)\n" + discord.getPermsRead().toString() + "```";
            new DiscordLogger(loggerBuilder, null);
            discord.setPermsRead(new StringBuilder());
        }

        if (discord.getPermsWrite().length() > 0) {
            loggerBuilder = "```Markdown\n# Permissions Error (Write)\n" + discord.getPermsWrite().toString() + "```";
            new DiscordLogger(loggerBuilder, null);
            discord.setPermsWrite(new StringBuilder());
        }

        if (discord.getPermsEmbeds().length() > 0) {
            loggerBuilder = "```Markdown\n# Permissions Error (Embed Links)\n" + discord.getPermsEmbeds().toString() + "```";
            new DiscordLogger(loggerBuilder, null);
            discord.setPermsEmbeds(new StringBuilder());
        }

        if (discord.getPermsManageMessages().length() > 0) {
            loggerBuilder = "```Markdown\n# Permissions Error (Manage Messages)\n" + discord.getPermsManageMessages().toString() + "```";
            new DiscordLogger(loggerBuilder, null);
            discord.setPermsManageMessages(new StringBuilder());
        }

        if (discord.getPermsEveryone().length() > 0) {
            loggerBuilder = "```Markdown\n# Permissions Error (Mention Everyone)\n" + discord.getPermsEveryone().toString() + "```";
            new DiscordLogger(loggerBuilder, null);
            discord.setPermsEveryone(new StringBuilder());
        }

        System.out.println("[SYSTEM] Cycle Complete. Waiting...");
    }

    private synchronized void editDeleteAnnouncements() {
        LocalDateTime timeNow = LocalDateTime.now();
        new DiscordLogger(" :poop: **Editing and Deleting Announcements...**", null);
        System.out.println("[SYSTEM] Editing and Deleting Announcements... " + timeNow);

        discord.offlineStream();
    }

    private synchronized void announceStreams() {
        LocalDateTime timeNow = LocalDateTime.now();
        new DiscordLogger(" :poop: **Announcing New Streams...**", null);
        System.out.println("[SYSTEM] Announcing New Streams... " + timeNow);

        discord.announceChannel("twitch");

    }
}
