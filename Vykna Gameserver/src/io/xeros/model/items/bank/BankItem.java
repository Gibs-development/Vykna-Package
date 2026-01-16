package io.xeros.model.items.bank;

import io.xeros.model.items.GameItem;

public class BankItem extends GameItem {

    public BankItem(int itemId, int itemAmount) {
        super(itemId, itemAmount);
    }

    public BankItem(int itemId) {
        this(itemId, 0);
    }


    /** Bank stacking key: same id AND same attrsHash */
    public boolean stacksWith(GameItem other) {
        if (other == null) return false;
        if (this.getId() != other.getId()) return false;

        // attrsHash 0 means "no attrs"
        return this.getAttrsHash() == other.getAttrsHash();
    }

}
