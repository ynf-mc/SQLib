package mrnavastar.sqlib.api;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import mrnavastar.sqlib.api.databases.Database;
import mrnavastar.sqlib.util.Parser;
import mrnavastar.sqlib.util.SqlManager;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

import java.util.UUID;

public class DataContainer {

    private Table table;
    private Database database;
    private final String id;

    public DataContainer(String id) {
        this.id = id;
    }

    public DataContainer(UUID id) {
        this.id = id.toString();
    }

    public DataContainer(int id) {
        this.id = String.valueOf(id);
    }

    public String getId() {
        return this.id;
    }

    public UUID getIdAsUuid() {
        return UUID.fromString(this.id);
    }

    public int getIdAsInt() {
        return Integer.parseInt(this.id);
    }

    public void link(Table table, Database database) {
        this.table = table;
        this.database = database;
    }

    private void putIntoDatabase(String type, String key, Object value) {
        if (!this.table.isInTransaction()) this.database.connect();
        JsonObject obj = SqlManager.readJson(this.table.getName(), this.id, type);
        if (obj == null) obj = new JsonObject();
        else obj.remove(key);
        if (type.equals("JSON")) obj.add(key, (JsonElement) value);
        else obj.addProperty(key, value.toString());
        SqlManager.writeJson(this.table.getName(), this.id, type, obj);
        if (!this.table.isInTransaction()) this.database.disconnect();
    }

    public void put(String key, String value) {
        putIntoDatabase("STRINGS", key, value);
    }

    public void put(String key, int value) {
        putIntoDatabase("INTS", key, value);
    }

    public void put(String key, float value) {
        putIntoDatabase("FLOATS", key, value);
    }

    public void put(String key, double value) {
        putIntoDatabase("DOUBLES", key, value);
    }

    public void put(String key, boolean value) {
        putIntoDatabase("BOOLEANS", key, value);
    }

    public void put(String key, JsonElement value) {
        putIntoDatabase("JSON", key, value);
    }

    public void put(String key, NbtElement value) {
        NbtCompound nbt = Parser.nbtFromString(getFromDatabase("NBT", "DATA").getAsString());
        if (nbt == null) nbt = new NbtCompound();
        nbt.put(key, value);
        putIntoDatabase("NBT", "DATA", nbt);
    }

    public void put(String key, BlockPos value) {
        putIntoDatabase("BLOCKPOS", key, value);
    }

    public void put(String key, UUID value) {
        putIntoDatabase("UUIDS", key, value);
    }

    public void put(String key, LiteralText value) {
        putIntoDatabase("LITERALTEXTS", key, value);
    }

    public void put(String key, MutableText value) {
        putIntoDatabase("MUTABLETEXTS", key, value);
    }

    public void put(String key, ItemStack value) {
        putIntoDatabase("ITEMSTACKS", key, value.getNbt());
    }

    private void dropFromDatabase(String type, String key) {
        if (!this.table.isInTransaction()) this.database.connect();
        JsonObject obj = SqlManager.readJson(this.table.getName(), this.id, type);
        if (obj != null) {
            obj.remove(key);
            SqlManager.writeJson(this.table.getName(), this.id, type, obj);
        }
        if (!this.table.isInTransaction()) this.database.disconnect();
    }

    public void dropString(String key) {
        dropFromDatabase("STRINGS", key);
    }

    public void dropInt(String key) {
        dropFromDatabase("INTS", key);
    }

    public void dropFloat(String key) {
        dropFromDatabase("FLOATS", key);
    }

    public void dropDouble(String key) {
        dropFromDatabase("DOUBLES", key);
    }

    public void dropBoolean(String key) {
        dropFromDatabase("BOOLEANS", key);
    }

    public void dropJson(String key) {
        dropFromDatabase("JSON", key);
    }

    public void dropNbt(String key) {
        NbtCompound nbt = Parser.nbtFromString(getFromDatabase("NBT", "DATA").getAsString());
        if (nbt != null) nbt.remove(key);
        putIntoDatabase("NBT", "DATA", nbt);
    }

    public void dropBlockPos(String key) {
        dropFromDatabase("BLOCKPOS", key);
    }

    public void dropUuid(String key) {
        dropFromDatabase("UUIDS", key);
    }

    public void dropLiteralText(String key) {
        dropFromDatabase("LITERALTEXTS", key);
    }

    public void dropMutableText(String key) {
        dropFromDatabase("MUTABLETEXTS", key);
    }

    public void dropItemStack(String key) {
        dropFromDatabase("ITEMSTACKS", key);
    }

    private JsonElement getFromDatabase(String type, String key) {
        if (!this.table.isInTransaction()) this.database.connect();
        JsonObject obj = SqlManager.readJson(this.table.getName(), this.id, type);
        if (!this.table.isInTransaction()) this.database.disconnect();
        if (obj == null) obj = new JsonObject();
        return obj.get(key);
    }

    public String getString(String key) {
        return getFromDatabase("STRINGS", key).getAsString();
    }

    public int getInt(String key) {
        return getFromDatabase("INTS", key).getAsInt();
    }

    public float getFloat(String key) {
        return getFromDatabase("FLOATS", key).getAsFloat();
    }

    public double getDouble(String key) {
        return getFromDatabase("DOUBLES", key).getAsDouble();
    }

    public boolean getBoolean(String key) {
        return getFromDatabase("BOOLEANS", key).getAsBoolean();
    }

    public JsonElement getJson(String key) {
        return getFromDatabase("JSON", key);
    }

    public NbtElement getNbt(String key) {
        NbtCompound nbt = Parser.nbtFromString(getFromDatabase("NBT", "DATA").getAsString());
        if (nbt != null) return nbt.get(key);
        return null;
    }

    public BlockPos getBlockPos(String key) {
        return Parser.blockPosFromString(getFromDatabase("BLOCKPOS", key).getAsString());
    }

    public UUID getUuid(String key) {
        return UUID.fromString(getFromDatabase("UUIDS", key).getAsString());
    }

    public LiteralText getLiteralText(String key) {
        return new LiteralText(getFromDatabase("LITERALTEXTS", key).getAsString());
    }

    public MutableText getMutableText(String key) {
        return Text.Serializer.fromJson(getFromDatabase("MUTABLETEXTS", key));
    }

    public ItemStack getItemStack(String key) {
        NbtCompound nbt = Parser.nbtFromString(getFromDatabase("ITEMSTACKS", key).getAsString());
        if (nbt != null) return ItemStack.fromNbt(nbt);
        return null;
    }
}