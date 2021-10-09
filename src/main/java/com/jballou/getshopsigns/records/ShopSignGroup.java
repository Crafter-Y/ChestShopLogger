package com.jballou.getshopsigns.records;

import java.util.List;

public class ShopSignGroup {
  private final String name;
  private final List<ShopSignEntry> entries;
  private final int order;
  private final boolean active;

  public ShopSignGroup(String name, List<ShopSignEntry> entries, int order, boolean active) {
    this.name = name;
    this.entries = entries;
    this.order = order;
    this.active = active;
  }

  public List<ShopSignEntry> getEntries() {
    return this.entries;
  }

  public int getOrder() {
    return this.order;
  }

  public boolean isActive() {
    return this.active;
  }

  public String getName() {
    return this.name;
  }
}
