package io.musician101.itembank.common.account.storage;

import io.musician101.musicianlibrary.java.storage.database.mongo.MongoSerializable;
import org.spongepowered.configurate.serialize.TypeSerializer;

public abstract class ItemStackSerializer<I> implements MongoSerializable<I>, TypeSerializer<I> {

}
