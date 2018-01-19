package io.musician101.itembank.common.account;

public class AbstractInventoryHandler<I, P, S> {

    protected final I inventory;
    protected final AccountPage<S> page;
    protected final P viewer;

    protected AbstractInventoryHandler(I inventory, AccountPage<S> page, P viewer) {
        this.inventory = inventory;
        this.page = page;
        this.viewer = viewer;
    }
}
