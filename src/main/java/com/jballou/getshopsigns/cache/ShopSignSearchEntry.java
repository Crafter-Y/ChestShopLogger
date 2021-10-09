package com.jballou.getshopsigns.cache;


import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import com.google.gson.*;

import com.jballou.getshopsigns.GetShopSigns;
import com.jballou.getshopsigns.records.ShopSignEntry;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import java.lang.reflect.*;
import java.util.*;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
//import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.StringNbtReader;
import com.jballou.getshopsigns.records.BasicColor;
import net.minecraft.util.math.BlockPos;
import org.lwjgl.system.CallbackI;

//import org.apache.commons.beanutils.BeanUtils;

public class ShopSignSearchEntry {
  public String posString = "";
  public Integer posHashCode = 0;
  public Integer x, y, z;
  public String sellerName = "";
  public String itemCode = "";
  public Integer itemQuantity = 0;
  public float priceBuy = 0;
  public float priceSell = 0;
  public Boolean canBuy = false;
  public Boolean canSell = false;

  public transient BlockPos blockPos;
  public transient String[] signText = new String[4];
  ShopSignSearchEntry(ShopSignEntry shopSignEntry) {
    Class fromClass = shopSignEntry.getClass();
    Class toClass = this.getClass();

    Field[] fields = fromClass.getDeclaredFields();
    for ( Field f : fields ) {
      try {
        Field t = toClass.getDeclaredField( f.getName() );

        if ( t.getType() == f.getType() ) {
          // extend this if to copy more immutable types if interested
          if ( t.getType() == String.class
                  || t.getType() == int.class || t.getType() == Integer.class
                  || t.getType() == char.class || t.getType() == Character.class) {
            f.setAccessible(true);
            t.setAccessible(true);
            t.set( this, f.get(shopSignEntry) );
          } else if ( t.getType() == Date.class  ) {
            // dates are not immutable, so clone non-null dates into the destination object
            Date d = (Date)f.get(shopSignEntry);
            f.setAccessible(true);
            t.setAccessible(true);
            t.set( this, d != null ? d.clone() : null );
          }
        }
      } catch (NoSuchFieldException ex) {
        // skip it
      } catch (IllegalAccessException ex) {
        GetShopSigns.LOGGER.error("Unable to copy field: {}", f.getName());
      }
    }
/*    this.posString = shopSignEntry.posString;
    this.posHashCode = shopSignEntry.posHashCode;
    this.x = shopSignEntry.x;
    this.y = shopSignEntry.y;
    this.z = shopSignEntry.z;

 */
  }

  // We don't care about the color and isDefault
  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || this.getClass() != o.getClass()) {
      return false;
    }
    ShopSignSearchEntry that = (ShopSignSearchEntry) o;
    return Objects.equal(this.posString, that.posString);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(this.posString);
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("posString", this.posString)
        .toString();
  }
}
