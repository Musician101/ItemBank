package io.musician101.itembank.common.account;

public class AbstractInventoryHandler<I, P, S, T> {

    protected T title;
    protected final I inventory;
    protected final AccountPage<S> page;
    protected final P viewer;
    protected boolean hasIllegalAmount = false;
    protected boolean hasIllegalItems = false;
    protected int itemAmount = 0;

    protected AbstractInventoryHandler(I inventory, T title, AccountPage<S> page, P viewer) {
        this.inventory = inventory;
        this.title = title;
        this.page = page;
        this.viewer = viewer;
    }
}
