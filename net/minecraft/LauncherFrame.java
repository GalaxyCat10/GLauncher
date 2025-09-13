/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.URLEncoder;
import javax.imageio.ImageIO;
import net.minecraft.Launcher;
import net.minecraft.LoginForm;
import net.minecraft.Util;

public class LauncherFrame
extends Frame {
    private static final long serialVersionUID = 1L;
    private Launcher launcher;
    private LoginForm loginForm;

    public LauncherFrame() {
        super("Minecraft Launcher");
        this.setBackground(Color.BLACK);
        this.loginForm = new LoginForm(this);
        this.setLayout(new BorderLayout());
        this.add((Component)this.loginForm, "Center");
        this.loginForm.setPreferredSize(new Dimension(854, 480));
        this.pack();
        this.setLocationRelativeTo(null);
        try {
            this.setIconImage(ImageIO.read(LauncherFrame.class.getResource("favicon.png")));
        }
        catch (IOException e1) {
            e1.printStackTrace();
        }
        this.addWindowListener(new WindowAdapter(){

            public void windowClosing(WindowEvent arg0) {
                new Thread(){

                    public void run() {
                        try {
                            Thread.sleep(30000L);
                        }
                        catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        System.out.println("FORCING EXIT!");
                        System.exit(0);
                    }
                }.start();
                if (LauncherFrame.this.launcher != null) {
                    LauncherFrame.this.launcher.stop();
                    LauncherFrame.this.launcher.destroy();
                }
                System.exit(0);
            }
        });
    }

    public void playCached(String userName) {
        try {
            if (userName == null || userName.length() <= 0) {
                userName = "Player";
            }
            this.launcher = new Launcher();
            this.launcher.customParameters.put("userName", userName);
            this.launcher.init();
            this.removeAll();
            this.add((Component)this.launcher, "Center");
            this.validate();
            this.launcher.start();
            this.loginForm = null;
            this.setTitle("Minecraft");
        }
        catch (Exception e) {
            e.printStackTrace();
            this.showError(e.toString());
        }
    }

    public void login(String userName, String password) {
        try {
            String parameters = "user=" + URLEncoder.encode(userName, "UTF-8") + "&password=" + URLEncoder.encode(password, "UTF-8");
            String result = Util.excutePost("http://www.minecraft.net/game/getversion.jsp", parameters);
            if (result == null) {
                this.loginForm.setNoNetwork();
                this.showError("Can't connect to minecraft.net");
                return;
            }
            if (!result.contains(":")) {
                if (result.trim().equals("Bad login")) {
                    this.showError("Login failed");
                } else if (result.trim().equals("Old version")) {
                    this.loginForm.setOutdated();
                    this.showError("Outdated launcher");
                } else {
                    this.showError(result);
                }
                return;
            }
            String[] values = result.split(":");
            this.launcher = new Launcher();
            this.launcher.customParameters.put("userName", userName);
            this.launcher.customParameters.put("latestVersion", values[0]);
            this.launcher.customParameters.put("downloadTicket", values[1]);
            this.launcher.init();
            this.removeAll();
            this.add((Component)this.launcher, "Center");
            this.validate();
            this.launcher.start();
            this.loginForm.loginOk();
            this.loginForm = null;
            this.setTitle("Minecraft");
        }
        catch (Exception e) {
            e.printStackTrace();
            this.showError(e.toString());
        }
    }

    private void showError(String error) {
        this.removeAll();
        this.add(this.loginForm);
        this.loginForm.setError(error);
        this.validate();
    }

    public boolean canPlayOffline(String userName) {
        return new Launcher().canPlayOffline();
    }

    public static void main(String[] args) {
        LauncherFrame launcherFrame = new LauncherFrame();
        launcherFrame.setVisible(true);
    }
}

