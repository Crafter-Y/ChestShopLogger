package com.jballou.getshopsigns.cache;

import com.jballou.getshopsigns.records.BasicColor;
import com.jballou.getshopsigns.records.ShopSignGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Physical representation of what we're searching for. Contains the actual state,
 * Scraps unneeded data and cleans up some logic along the way.
 * <p>
 * I control when this list is populated and has changes through the first load
 * and the gui.
 */
public class ShopSignSearchCache {
  private List<ShopSignSearchEntry> cache = new ArrayList<>();

  public void processGroupedList(List<ShopSignGroup> shopSignEntries) {
    // Flatten the grouped list down to a single cacheable list
    this.cache = shopSignEntries.stream()
            .flatMap(e -> e.getEntries().stream()
                    .map(a -> new ShopSignSearchEntry(a)
                    ))
            .distinct()
            .collect(Collectors.toList());
  }

  public List<ShopSignSearchEntry> get() {
    return this.cache;
  }
}
