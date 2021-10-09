package com.jballou.getshopsigns.storage;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import net.minecraft.client.MinecraftClient;
import com.jballou.getshopsigns.GetShopSigns;

import java.io.*;
import java.lang.reflect.Type;

public abstract class Store<T> {
  private static final String CONFIG_PATH = String.format("%s/config/%s", MinecraftClient.getInstance().runDirectory, GetShopSigns.MOD_ID);

  private final String name;
  private final String file;

  Store(String name) {
    this.name = name;
    this.file = String.format("%s/%s.json", CONFIG_PATH, this.name);

    this.read();
  }

  public T read() {
    Gson gson = new Gson();
    GetShopSigns.LOGGER.info(String.format("Reading %s from JSON %s", this.name, this.file));

    try {
      try {
        T t = gson.fromJson(new FileReader(this.file), this.getType());
        System.out.println(t);
        return t;
      } catch (JsonIOException | JsonSyntaxException e) {
        GetShopSigns.LOGGER.fatal("Fatal error with json loading on {}.json", this.name, e);
      }
    } catch (FileNotFoundException ignored) {
      // Write a blank version of the file
      if (new File(CONFIG_PATH).mkdirs()) {
        this.write();
      }
    }

    return null;
  }

  public void write() {
    GetShopSigns.LOGGER.info(String.format("Writing %s to JSON %s", this.name, this.file));
    Gson gson = new GsonBuilder()
        .setPrettyPrinting()
        .create();

    try {
      try (FileWriter writer = new FileWriter(this.file)) {
        gson.toJson(this.get(), writer);
        writer.flush();
      }
    } catch (IOException | JsonIOException e) {
      GetShopSigns.LOGGER.catching(e);
    }
  }

  public abstract T get();

  abstract Type getType();
}
