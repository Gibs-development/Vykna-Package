package io.xeros.model.items.bank;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 *
 * @author Jason http://www.rune-server.org/members/jason
 * @date Apr 11, 2014
 */
public class BankTab {

	CopyOnWriteArrayList<BankItem> bankItems = new CopyOnWriteArrayList<>();

	private int tabId;
	private final Bank bank;

	public BankTab(int tabId, Bank bank) {
		this.setTabId(tabId);
		this.bank = bank;
	}

	/**
	 * Add item to this tab.
	 * IMPORTANT: stacksWith MUST include attrsHash for augmented items.
	 */
	public void add(BankItem bankItem) {
		if (bankItem == null) return;
		if (bankItem.getAmount() < 0) return;

		for (BankItem item : bankItems) {
			if (item != null && item.stacksWith(bankItem)) {
				long total = (long) item.getAmount() + (long) bankItem.getAmount();
				item.setAmount(total > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) total);
				return;
			}
		}

		bankItems.add(bankItem);
	}

	/**
	 * Remove amount of a matching item from this tab.
	 *
	 * type:
	 * 0 = normal withdraw/remove
	 * 1 = placeholder release (amt 0)
	 */
	public void remove(BankItem bankItem, int type, boolean placeHolder) {
		if (bankItem == null) return;

		Collection<BankItem> toRemove = new ArrayList<>();

		BankItem target = null;

		// 1) If caller provided attrs, require exact match
		if (bankItem.hasAttrs()) {
			for (BankItem item : bankItems) {
				if (item == null) continue;
				if (item.stacksWith(bankItem)) {
					target = item;
					break;
				}
			}
		} else {
			// 2) Fallback: caller didn't provide attrs (legacy paths)
			// Prefer removing a non-augmented stack (attrsHash==0) if present.
			for (BankItem item : bankItems) {
				if (item == null) continue;
				if (item.getId() != bankItem.getId()) continue;

				if (!item.hasAttrs() || item.getAttrsHash() == 0) {
					target = item;
					break;
				}
			}
			// If none found, fall back to first ID match
			if (target == null) {
				for (BankItem item : bankItems) {
					if (item == null) continue;
					if (item.getId() == bankItem.getId()) {
						target = item;
						break;
					}
				}
			}
		}

		if (target == null) return;

		int removeAmt = bankItem.getAmount();

		// Placeholder release is special: remove the entry entirely when type=1
		if (type == 1) {
			toRemove.add(target);
			bankItems.removeAll(toRemove);
			return;
		}

		// Normal remove
		if (target.getAmount() - removeAmt <= 0) {
			if (placeHolder && type == 0) {
				target.setAmount(0); // keep placeholder
			} else {
				toRemove.add(target);
			}
		} else {
			target.setAmount(target.getAmount() - removeAmt);
			if (target.getAmount() <= 0 && placeHolder) {
				target.setAmount(0);
			}
		}

		bankItems.removeAll(toRemove);
	}


	/**
	 * Slot-safe remove: removes from the exact slot you clicked.
	 * This avoids any risk of "first matching id" behaviour.
	 */
	public void removeAtSlot(int slot, int amount, boolean placeHolder) {
		if (slot < 0 || slot >= bankItems.size()) return;

		BankItem item = bankItems.get(slot);
		if (item == null) return;

		if (amount < 0) return;

		// placeholder release (amount 0 & "release placeholder") should be done via remove(type=1),
		// but we keep this safe anyway.
		if (item.getAmount() == 0) {
			// releasing placeholder removes the entry
			bankItems.remove(slot);
			return;
		}

		int removeAmt = Math.min(amount, item.getAmount());

		if (item.getAmount() - removeAmt <= 0) {
			if (placeHolder) {
				item.setAmount(0);
			} else {
				bankItems.remove(slot);
			}
		} else {
			item.setAmount(item.getAmount() - removeAmt);
		}
	}

	public int size() {
		return bankItems.size();
	}

	public int freeSlots() {
		if (bank.getItemCount() >= bank.getBankCapacity()) {
			return 0;
		} else {
			return bank.getBankCapacity() - bank.getItemCount();
		}
	}

	public boolean contains(BankItem bankItem) {
		if (bankItem == null) return false;
		for (int i = 0; i < bankItems.size(); i++) {
			BankItem it = bankItems.get(i);
			if (it != null && it.stacksWith(bankItem)) {
				return true;
			}
		}
		return false;
	}

	public boolean containsAmount(BankItem bankItem) {
		if (bankItem == null) return false;
		for (int i = 0; i < bankItems.size(); i++) {
			BankItem it = bankItems.get(i);
			if (it != null && it.stacksWith(bankItem)) {
				return it.getAmount() >= bankItem.getAmount();
			}
		}
		return false;
	}

	public boolean spaceAvailable(BankItem bankItem) {
		if (bankItem == null) return false;

		for (int i = 0; i < bankItems.size(); i++) {
			BankItem it = bankItems.get(i);
			if (it != null && it.stacksWith(bankItem)) {
				long total = (long) it.getAmount() + (long) bankItem.getAmount();
				return total <= Integer.MAX_VALUE;
			}
		}

		return true;
	}

	public int getItemAmount(BankItem bankItem) {
		if (bankItem == null) return 0;
		for (BankItem it : bankItems) {
			if (it != null && it.stacksWith(bankItem)) {
				return it.getAmount();
			}
		}
		return 0;
	}

	public BankItem getItem(int slot) {
		if (slot < bankItems.size()) {
			return bankItems.get(slot);
		}
		return null;
	}

	public BankItem getItem(BankItem item) {
		if (item == null) return null;
		for (BankItem it : bankItems) {
			if (it != null && it.stacksWith(item)) {
				return it;
			}
		}
		return null;
	}

	public void setItem(int slot, BankItem item) {
		bankItems.set(slot, item);
	}

	public CopyOnWriteArrayList<BankItem> getItems() {
		return bankItems;
	}

	public int getTabId() {
		return tabId;
	}

	public void setTabId(int tabId) {
		this.tabId = tabId;
	}
}
