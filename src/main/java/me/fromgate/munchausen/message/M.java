/*
 *  ReActions, Minecraft bukkit plugin
 *  (c)2012-2017, fromgate, fromgate@gmail.com
 *  http://dev.bukkit.org/server-mods/reactions/
 *
 *  This file is part of ReActions.
 *
 *  ReActions is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  ReActions is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with ReActions.  If not, see <http://www.gnorg/licenses/>.
 *
 */

package me.fromgate.munchausen.message;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public enum M {

    //Default (lang) messages
    LNG_LOAD_FAIL("Failed to load languages from file. Default message used"),
    LNG_SAVE_FAIL("Failed to save lang file"),
    LNG_PRINT_FAIL("Failed to print message %1%. Sender object is null."),
    LNG_CONFIG("[MESSAGES] Messages: %1% Language: %2% Save translate file: %1% Debug mode: %3%"),
    LNG_PRINT_FAIL_M("Failed to print message. Unknown key %1%"),
    LNG_TRANSLATION_NOT_FOUND("Failed to find localized message %1%"),
    ENABLED("enabled"),
    DISABLED("disabled"),

    WORD_UNKNOWN("Unknown"),
    PERMISSION_FAIL("You have not enough permissions to execute this command", 'c'),
    PLAYER_COMMAD_ONLY("You can use this command in-game only!", 'c'),
    CMD_REGISTERED("Command registered: %1%"),
    CMD_FAILED("Failed to execute command. Type %1% to get help!"),
    HLP_TITLE("%1% | Help"),

    MSG_OUTDATED("%1% is outdated! Recommended version is %2%", 'e', '6'),
    MSG_PLEASEDOWNLOAD("Please download new version from:"),


    CMD_HELP ("%1% - show help page"),
    CMD_GIVE ("%1% - open virtual inventory contained Munchausen Fireworks items"),
    CMD_RELOAD ("%1% - reload configuration"),

    HELP_TITLE ("ReActions | Help page!",'6'),

    GIVE_INV_TITLE ("Munchausen Fireworks",'5'),
    MSG_RELOADED ("Configuration successfully reloaded"),
    ;

    private static Messenger messenger;

    private static boolean debugMode = false;
    private static String language = "default";
    private static String pluginName;
    private static Set onceLog = new HashSet();


    public static String colorize(String text) {
        return messenger.colorize(text);
    }


    /**
     * This is my favorite debug routine :) I use it everywhere to print out variable values
     *
     * @param s - array of any object that you need to print out.
     *          Example:
     *          Message.BC ("variable 1:",var1,"variable 2:",var2)
     */
    public static void BC(Object... s) {
        if (!debugMode) return;
        if (s.length == 0) return;
        StringBuilder sb = new StringBuilder("&3[").append(pluginName).append("]&f ");
        for (Object str : s) {
            sb.append(str == null ? "null" : str.toString()).append(" ");
        }

        messenger.broadcast(colorize(sb.toString().trim()));
    }

    /**
     * Send current message to log files
     *
     * @param s
     * @return — always returns true.
     * Examples:
     * Message.ERROR_MESSAGE.log(variable1); // just print in log
     * return Message.ERROR_MESSAGE.log(variable1); // print in log and return value true
     */
    public boolean log(Object... s) {
        M.logMessage(getText(s));
        return true;
    }

    /**
     * Same as log, but will printout nothing if debug mode is disabled
     *
     * @param s
     * @return — always returns true.
     */
    public boolean debug(Object... s) {
        if (debugMode)
            log(messenger.clean(getText(s)));
        return true;
    }

    /**
     * Show a message to player in center of screen (this routine unfinished yet)
     *
     * @param seconds — how much time (in seconds) to show message
     * @param sender  — Player
     * @param s
     * @return — always returns true.
     */
    public boolean tip(int seconds, Object sender, Object... s) {
        return messenger.tip(seconds, sender, getText(s));
    }
    /*
    public boolean tip(int seconds, CommandSender sender, Object... s) {
        if (sender == null) return Message.LNG_PRINT_FAIL.log(this.name());
        final Player player = sender instanceof Player ? (Player) sender : null;
        final String message = getText(s);
        if (player == null) sender.sendMessage(message);
        else for (int i = 0; i < seconds; i++)
            Server.getInstance().getScheduler().scheduleDelayedTask(new Runnable() {
                public void run() {
                    if (player.isOnline()) player.sendTip(message);
                }
            }, 20 * i);
        return true;
    } */

    /**
     * Show a message to player in center of screen
     *
     * @param sender — Player
     * @param s
     * @return — always returns true.
     */
    public boolean tip(Object sender, Object... s) {
        return messenger.tip(sender, getText(s));
    }

    /**
     * Send message to Player or to ConsoleSender
     *
     * @param sender
     * @param s
     * @return — always returns true.
     */
    public boolean print(Object sender, Object... s) {
        if (sender == null) return M.LNG_PRINT_FAIL.log(this.name());
        return messenger.print(sender, getText(s));
    }

    /**
     * Send message to all players or to players with defined permission
     *
     * @param permission
     * @param s
     * @return — always returns true.
     * <p>
     * Examples:
     * Message.MSG_BROADCAST.broadcast ("pluginname.broadcast"); // send message to all players with permission "pluginname.broadcast"
     * Message.MSG_BROADCAST.broadcast (null); // send message to all players
     */
    public boolean broadcast(String permission, Object... s) {
        return messenger.broadcast(permission, getText(s));
    }


    /**
     * Get formated text.
     *
     * @param keys * Keys - are parameters for message and control-codes.
     *             Parameters will be shown in position in original message according for position.
     *             This keys are used in every method that prints or sends message.
     *             <p>
     *             Example:
     *             <p>
     *             EXAMPLE_MESSAGE ("Message with parameters: %1%, %2% and %3%");
     *             Message.EXAMPLE_MESSAGE.getText("one","two","three"); //will return text "Message with parameters: one, two and three"
     *             <p>
     *             * Color codes
     *             You can use two colors to define color of message, just use character symbol related for color.
     *             <p>
     *             Message.EXAMPLE_MESSAGE.getText("one","two","three",'c','4');  // this message will be red, but word one, two, three - dark red
     *             <p>
     *             * Control codes
     *             Control codes are text parameteres, that will be ignored and don't shown as ordinary parameter
     *             - "SKIPCOLOR" - use this to disable colorizing of parameters
     *             - "NOCOLOR" (or "NOCOLORS") - return uncolored text, clear all colors in text
     *             - "FULLFLOAT" - show full float number, by default it limit by two symbols after point (0.15 instead of 0.1483294829)
     * @return
     */
    public String getText(Object... keys) {
        char c2 = '2';
        char c1 = 'a';
        char[] colors = new char[]{color1 == null ? c1 : color1, color2 == null ? c2 : color2};
        if (keys.length == 0) {
            return colorize("&" + colors[0] + this.message);
        }
        String str = this.message;
        boolean noColors = false;
        boolean skipDefaultColors = false;
        boolean fullFloat = false;
        String prefix = "";
        int count = 1;
        int c = 0;
        DecimalFormat fmt = new DecimalFormat("####0.##");
        for (Object key : keys) {
            String s = messenger.toString(key, fullFloat);//keys[i].toString();
            if (c < 2 && key instanceof Character) {
                colors[c] = (Character) key;
                c++;
                continue;
            } else if (s.startsWith("prefix:")) {
                prefix = s.replace("prefix:", "");
                continue;
            } else if (s.equals("SKIPCOLOR")) {
                skipDefaultColors = true;
                continue;
            } else if (s.equals("NOCOLORS") || s.equals("NOCOLOR")) {
                noColors = true;
                continue;
            } else if (s.equals("FULLFLOAT")) {
                fullFloat = true;
                continue;
            } else if (key instanceof Double) {
                if (!fullFloat) s = fmt.format((Double) key);
            } else if (key instanceof Float) {
                if (!fullFloat) s = fmt.format((Float) key);
            }

            String from = (new StringBuilder("%").append(count).append("%")).toString();
            String to = skipDefaultColors ? s : (new StringBuilder("&").append(colors[1]).append(s).append("&").append(colors[0])).toString();
            str = str.replace(from, to);
            count++;
        }
        str = colorize(prefix.isEmpty() ? "&" + colors[0] + str : prefix + " " + "&" + colors[0] + str);
        if (noColors) str = clean(str);
        return str;
    }

    public static String clean(String str) {
        return messenger.clean(str);
    }

    private void initMessage(String message) {
        this.message = message;
    }

    private String message;
    private Character color1;
    private Character color2;

    M(String msg) {
        message = msg;
        this.color1 = null;
        this.color2 = null;
    }

    M(String msg, char color1, char color2) {
        this.message = msg;
        this.color1 = color1;
        this.color2 = color2;
    }

    M(String msg, char color) {
        this(msg, color, color);
    }

    @Override
    public String toString() {
        return this.getText("NOCOLOR");
    }

    /**
     * Initialize current class, load messages, etc.
     * Call this file in onEnable method after initializing plugin configuration
     */
    public static void init(String pluginName, Messenger mess, String lang, boolean debug, boolean save) {
        M.pluginName = pluginName;
        messenger = mess;
        language = lang;
        debugMode = debug;
        boolean saveLanguage = save;
        initMessages();
        if (saveLanguage) saveMessages();
        LNG_CONFIG.debug(M.values().length, language, true, debugMode);
    }

    /**
     * Enable debugMode
     *
     * @param debug
     */
    public static void setDebugMode(boolean debug) {
        debugMode = debug;
    }

    public static boolean isDebug() {
        return debugMode;
    }


    private static void initMessages() {
        Map<String, String> lng = messenger.load(language);
        for (M key : M.values()) {
            if (lng.containsKey(key.name().toLowerCase())) {
                key.initMessage(lng.get(key.name().toLowerCase()));
            } else if (!(language.equalsIgnoreCase("default") || language.equalsIgnoreCase("english"))) {
                M.LNG_TRANSLATION_NOT_FOUND.log(key.name());
            }
        }
    }

    private static boolean exists(String key) {
        for (M m : values()) {
            if (m.name().equalsIgnoreCase(key)) return true;
        }
        return false;
    }

    private static void saveMessages() {
        Map<String, String> messages = new LinkedHashMap<>();
        for (M msg : M.values()) {
            messages.put(msg.name().toLowerCase(), msg.message);
        }
        messenger.save(language, messages);
    }

    /**
     * Send message (formed using join method) to server log if debug mode is enabled
     *
     * @param s
     */
    public static boolean debugMessage(Object... s) {
        if (debugMode) messenger.log(clean(join(s)));
        return true;
    }

    /**
     * Join object array to string (separated by space)
     *
     * @param s
     */
    public static String join(Object... s) {
        StringBuilder sb = new StringBuilder();
        for (Object o : s) {
            if (sb.length() > 0) sb.append(" ");
            sb.append(messenger.toString(o, false));
        }
        return sb.toString();
    }

    public static void printLines(Object sender, Collection<String> lines) {
        for (String l : lines) {
            messenger.print(sender, colorize(l));
        }
    }

    public static void printPage(Object sender, List<String> lines, M title, int pageNum, int linesPerPage) {
        printPage(sender, lines, title, pageNum, linesPerPage, false);
    }

    public static void printPage(Object sender, List<String> lines, M title, int pageNum, int linesPerPage, boolean showNum) {
        printPage(sender, lines, title, null, pageNum, linesPerPage, showNum);
    }

    public static void printPage(Object sender, List<String> lines, M title, M footer, int pageNum, int linesPerPage) {
        printPage(sender, lines, title, footer, pageNum, linesPerPage, false);

    }

    public static void printPage(Object sender, List<String> lines, M title, M footer, int pageNum, int linesPerPage, boolean showNum) {
        if (lines == null || lines.isEmpty()) return;
        List<String> page = new ArrayList<>();
        if (title != null) page.add(title.getText('e', '6', pluginName));

        int pageCount = lines.size() / linesPerPage + 1;
        if (pageCount * linesPerPage == lines.size()) pageCount = pageCount - 1;

        int num = pageNum <= pageCount ? pageNum : 1;

        for (int i = linesPerPage * (num - 1); i < Math.min(lines.size(), num * linesPerPage); i++) {
            page.add((showNum ? (i + 1) : "") + lines.get(i));
        }
        if (footer != null) page.add(footer.getText('e', 'e', num, pageCount));
        printLines(sender, page);
    }

    public static boolean logMessage(Object... s) {
        messenger.log(clean(join(s)));
        return true;
    }


    public static M getByName(String name) {
        for (M m : values()) {
            if (m.name().equalsIgnoreCase(name)) return m;
        }
        return null;
    }

    public static String enDis(boolean value) {
        return value ? M.ENABLED.toString() : M.DISABLED.toString();
    }

    public static void logOnce(String key, Object... s) {
        if (onceLog.contains(key)) return;
        onceLog.add(key);
        M.logMessage(s);
    }

    public static void printMessage(Object sender, String message) {
        if (messenger.isValidSender(sender)) {
            messenger.print(sender, colorize(message));
        }
    }




}
