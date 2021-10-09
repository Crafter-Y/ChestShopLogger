package com.jballou.getshopsigns.storage;

import com.google.gson.reflect.TypeToken;
import com.jballou.getshopsigns.cache.ShopSignSearchCache;
import com.jballou.getshopsigns.records.ShopSignGroup;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ShopSignStore extends Store<List<ShopSignGroup>> {
  private static ShopSignStore instance;
  private final ShopSignSearchCache cache = new ShopSignSearchCache();
  private List<ShopSignGroup> shopSignEntries = new ArrayList<>();

  private ShopSignStore() {
    super("shopsigns");

    List<ShopSignGroup> entries = this.read();
    if (entries == null) {
      return;
    }

    this.shopSignEntries = entries;
    this.updateCache(entries);
  }

  static ShopSignStore getInstance() {
    if (instance == null) {
      instance = new ShopSignStore();
    }

    return instance;
  }

  private void updateCache(List<ShopSignGroup> data) {
    this.cache.processGroupedList(data);
  }

  @Override
  public List<ShopSignGroup> get() {
    return this.shopSignEntries;
  }

  @Override
  Type getType() {
    return new TypeToken<List<ShopSignGroup>>() {}.getType();
  }
}
