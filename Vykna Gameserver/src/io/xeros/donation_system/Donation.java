package io.xeros.donation_system;

import io.xeros.Configuration;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.PlayerHandler;
import io.xeros.util.discord.Discord;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


/**
 *
 * Author C.T
 *
 * Using this class:
 * To call this class, it's best to make a new thread. You can do it below like so:
 * new Thread(new Donation(player)).start();
 */
public class Donation implements Runnable {

	public static final String HOST = "91.238.164.175"; // website ip address
	public static final String USER = "runerogu";
	public static final String PASS = "Thorpey123";
	public static final String DATABASE = "runerogu_store";

	private Player player;
	private Connection conn;
	private Statement stmt;

	/**
	 * The constructor
	 *
	 * @param player
	 */
	public Donation(Player player) {
		this.player = player;
	}


	@Override
	public void run() {
		try {
			if (!connect(HOST, DATABASE, USER, PASS)) {
				System.err.println("Failed connecting to db");
				return;
			}

			String name = player.getLoginName().replace("_", " ");
			ResultSet rs = executeQuery(
					"SELECT * FROM orders WHERE username='" + name + "' AND complete=1 AND claimed=0");
			boolean claimed = false, banked = false;
			while (rs.next()) {
				int item_id = rs.getInt("item_id");
				double paid = rs.getDouble("discount");
				int quantity = rs.getInt("amount");
				int space = player.getInventory().freeInventorySlots();
				player.getInventory().freeInventorySlots();

				if (space >= quantity) {
					player.getItems().addItem(item_id, quantity);
					Discord.writeServerSyncMessage("["+player.getLoginName()+"]  has donated for "+item_id+ " X "+quantity+" for "+paid );
					PlayerHandler.executeGlobalMessage("@red@"+player.getLoginName()+" @blu@has just donated to help the server grow! @red@::store");
				}else {
					player.getItems().addItemToBankOrDrop(item_id, quantity);
					Discord.writeServerSyncMessage("["+player.getLoginName()+"]  has donated for "+item_id+ " X "+quantity+" for "+paid );
					PlayerHandler.executeGlobalMessage("@red@"+player.getLoginName()+" @blu@has just donated to help the server grow! @red@::store");
					banked = true;
				}

				claimed = true;
				rs.updateInt("claimed", 1);
				rs.updateRow();
			}
			if (claimed) {
				player.sendMessage("Thank you for donating, your items have been added to your "
						+ (banked ? "bank" : "inventory") + ".");
				player.updateRank();
			} else {
				player.getDH().sendNpcChat1("You have no awaiting claims...", 9120, "Donator shop");
			}
			player.nextChat = -1;
			destroy();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 *
	 * @param host
	 *            the host ip address or url
	 * @param database
	 *            the name of the database
	 * @param user
	 *            the user attached to the database
	 * @param pass
	 *            the users password
	 * @return true if connected
	 */
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

	/**
	 * Disconnects from the MySQL server and destroy the connection
	 * and statement instances
	 */
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

	/**
	 * Executes an update query on the database
	 * @param query
	 * @see {@link Statement#executeUpdate}
	 */
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

	/**
	 * Executres a query on the database
	 * @param query
	 * @see {@link Statement#executeQuery(String)}
	 * @return the results, never null
	 */
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
