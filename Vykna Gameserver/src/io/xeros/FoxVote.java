package io.xeros;


import io.xeros.achievements.AchievementHandler;
import io.xeros.achievements.AchievementList;
import io.xeros.content.Announcement;
import io.xeros.content.achievement.AchievementType;
import io.xeros.content.achievement.Achievements;
import io.xeros.model.entity.player.Player;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class FoxVote implements Runnable {

    public static final String HOST = "91.238.164.175"; // website ip address
    public static final String USER = "runerogu";// database username
    public static final String PASS = "Thorpey123";// database password
    public static final String DATABASE = "runerogu_vote";// database name

    private Player player;
    private Connection conn;
    private Statement stmt;

    public FoxVote(Player player) {
        this.player = player;
    }

    @Override
    public void run() {
        try {
            if (!connect(HOST, DATABASE, USER, PASS)) {
                System.err.println("Failed connecting to db");
                return;
            }
            int amount = 0;
            String name = player.getLoginName().replace("_", " ");
            ResultSet rs = executeQuery("SELECT * FROM fx_votes WHERE username='"+name+"' AND claimed=0 AND callback_date IS NOT NULL");

            while (rs.next()) {

                String timestamp = rs.getTimestamp("callback_date").toString();
                String ipAddress = rs.getString("ip_address");
                int siteId = rs.getInt("site_id");
                amount++;
                rs.updateInt("claimed", 1); // do not delete otherwise they can reclaim!
                System.out.println("[Vote] Vote claimed by "+name+". (sid: "+siteId+", ip: "+ipAddress+") on: "+timestamp+") ");
                rs.updateRow();
            }
            if (amount > 0) {
                player.getItems().addItemUnderAnyCircumstance(1464, amount);
                Configuration.VOTES_CLAIMED++;
                player.sendMessage("<col=006600>You successfully claim "+amount+"x vote tickets!");
                Achievements.increase(player, AchievementType.VOTER, 1);
                AchievementHandler.activate(player, AchievementList.CLAIM_5_VOTES, 1);//NEW ACHIEVEMNTS

                if (player.hasFollower && (player.petSummonId == 30023)) {//vote pet
                    player.getItems().addItemUnderAnyCircumstance(1464, amount);
                    player.sendMessage("Thank you for voting! Your pet has @red@x2 @bla@your votes.");
                }

                if (amount > 2 && Configuration.BONUS_VOTES) {
                    player.getItems().addItemUnderAnyCircumstance(1464, 1);
                    player.sendMessage("<col=006600>You receive an extra vote ticket due to the ongoing vote event!");
                }
                amount = 0;
            } else {
                player.sendMessage("@red@You have no vote rewards waiting for you.");
            }
            if (Configuration.VOTES_CLAIMED >= 5) {
                Announcement.announce("@cr11@[<col=006600>News</col>]: Another 5 voters have claimed their rewards! <col=006600>::vote");
                Configuration.VOTES_CLAIMED = 0;
            }
            destroy();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public boolean connect(String host, String database, String user, String pass) {
        try {
            this.conn = DriverManager.getConnection("jdbc:mysql://"+host+":3306/"+database, user, pass);
            System.out.println("connecting to database "+host+"!");
            return true;
        } catch (SQLException e) {
            System.out.println("Failing connecting to database!");
            return false;
        }
    }

    public void destroy() {
        try {
            conn.close();
            conn = null;
            if (stmt != null) {
                stmt.close();
                stmt = null;
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public int executeUpdate(String query) {
        try {
            this.stmt = this.conn.createStatement(1005, 1008);
            int results = stmt.executeUpdate(query);
            return results;
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return -1;
    }

    public ResultSet executeQuery(String query) {
        try {
            this.stmt = this.conn.createStatement(1005, 1008);
            ResultSet results = stmt.executeQuery(query);
            return results;
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return null;
    }

}