package io.musician101.itembank.common.account.storage.file;

import io.musician101.itembank.common.Reference.Messages;
import io.musician101.itembank.common.Reference.PlayerData;
import io.musician101.itembank.common.account.Account;
import io.musician101.itembank.common.account.AccountWorld;
import io.musician101.itembank.common.account.storage.AccountStorage;
import io.musician101.itembank.common.account.storage.ItemStackParsing;
import java.io.File;
import java.io.IOException;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.SimpleConfigurationNode;

public class AccountFileStorage<I> extends AccountStorage<I> {

    @Nonnull
    private final ConfigurateLoader loader;
    @Nonnull
    private final File storageDir;
    @Nonnull
    private final String extension;

    public AccountFileStorage(@Nonnull File storageDir, @Nonnull ConfigurateLoader loader, @Nonnull String extension, @Nonnull ItemStackParsing<I> itemStackParsing) {
        super(itemStackParsing);
        this.storageDir = storageDir;
        this.loader = loader;
        this.extension = extension;
    }

    @Override
    public @Nonnull List<String> save() {
        List<String> errors = new ArrayList<>();
        getAccounts().forEach(account -> {
            ConfigurationNode node = SimpleConfigurationNode.root();
            node.getNode(PlayerData.ID).setValue(account.getID());
            node.getNode(PlayerData.NAME).setValue(account.getName());
            node.getNode(PlayerData.WORLDS).setValue(mapWorlds(account.getWorlds()));
            File file = getFile(account.getID());
            try {
                loader.loader(file.toPath()).save(node);
            }
            catch (IOException e) {
                errors.add(Messages.fileLoadFail(file));
            }
        });

        return errors;
    }

    private List<ConfigurationNode> mapWorlds(List<AccountWorld<I>> worlds) {
        return worlds.stream().map(world -> {
            ConfigurationNode node = SimpleConfigurationNode.root();
            node.getNode(PlayerData.WORLD).setValue(world.getWorldName());
            node.getNode(PlayerData.PAGES).setValue(mapPages(world.getPages()));
            return node;
        }).collect(Collectors.toList());
    }

    private List<ConfigurationNode> mapPages(List<I[]> pages) {
        return IntStream.range(0, pages.size()).mapToObj(x -> {
            ConfigurationNode node = SimpleConfigurationNode.root();
            node.getNode(PlayerData.PAGE).setValue(x);
            node.getNode(PlayerData.ITEMS).setValue(Arrays.stream(pages.get(x), 0, 45).map(itemStackParsing::save).collect(Collectors.toList()));
            return node;
        }).collect(Collectors.toList());
    }

    @Override
    public @Nonnull List<String> load() {
        File[] files = getStorageDir().listFiles();
        if (files == null) {
            return Collections.emptyList();
        }

        List<String> errors = new ArrayList<>();
        Stream.of(files).filter(file -> file.getName().endsWith(extension)).forEach(file -> {
            try {
                ConfigurationNode node = loader.loader(file.toPath()).load();
                UUID uuid = UUID.fromString(node.getNode(PlayerData.ID).getString());
                String name = node.getNode(PlayerData.NAME).getString();
                Account<I> account = new Account<>(uuid, name);
                node.getNode(PlayerData.WORLDS).getList(this::mapWorld).forEach(account::setWorld);
                setAccount(account);
            }
            catch (IOException e) {
                errors.add(Messages.fileLoadFail(file));
            }
        });

        return errors;
    }

    private AccountWorld<I> mapWorld(Object o) {
        ConfigurationNode node = (ConfigurationNode) o;
        AccountWorld<I> accountWorld = new AccountWorld<>(node.getNode(PlayerData.WORLD).getString());
        TreeMap<Integer, I[]> map = new TreeMap<>(Integer::compareTo);
        node.getNode(PlayerData.PAGES).getList(this::mapPage).forEach(p -> map.put(p.getKey(), p.getValue()));
        IntStream.range(0, map.lastKey()).forEach(i -> accountWorld.setPage(i, map.getOrDefault(i, itemStackParsing.emptyArray())));
        return accountWorld;
    }

    private Entry<Integer, I[]> mapPage(Object o) {
        ConfigurationNode node = (ConfigurationNode) o;
        return new SimpleEntry<>(node.getNode(PlayerData.PAGE).getInt(), node.getNode(PlayerData.ITEMS).getValue(this::mapItemStacks));
    }

    private I[] mapItemStacks(Object o) {
        return ((ConfigurationNode) o).getList(this::mapItemStack).toArray(itemStackParsing.emptyArray());
    }

    private I mapItemStack(Object o) {
        return itemStackParsing.load((ConfigurationNode) o);
    }

    @Nonnull
    protected File getStorageDir() {
        return storageDir;
    }

    @Nonnull
    protected File getFile(@Nonnull UUID uuid) {
        return new File(storageDir, uuid.toString() + extension);
    }
}
