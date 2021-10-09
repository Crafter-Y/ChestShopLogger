package com.jballou.getshopsigns.storage;

public class Stores {
  public static final SettingsStore SETTINGS = SettingsStore.getInstance();
  public static final BlockStore BLOCKS = BlockStore.getInstance();
  public static final ShopSignStore SHOPSIGNS = ShopSignStore.getInstance();

  public static void reload() {
    SETTINGS.read();
    BLOCKS.read();
    SHOPSIGNS.read();
  }

  public static void write() {
    SETTINGS.write();
    BLOCKS.write();
    SHOPSIGNS.write();
  }
}
