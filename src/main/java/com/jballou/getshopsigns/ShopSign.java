package com.jballou.getshopsigns;

import java.util.regex.*;
import java.util.*;

import net.fabricmc.api.*;
import net.fabricmc.fabric.api.client.rendering.v1.*;
import net.minecraft.block.*;
import net.minecraft.block.entity.*;
import net.minecraft.client.*;
import net.minecraft.client.gui.*;
import net.minecraft.client.util.math.*;
import net.minecraft.entity.*;
import net.minecraft.entity.decoration.*;
import net.minecraft.entity.projectile.*;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SignItem;
import net.minecraft.text.*;
//import net.minecraft.tileentity.TileEntity;
//import net.minecraft.tileentity.SignTileEntity;
import net.minecraft.util.Formatting;
import net.minecraft.util.hit.*;
import net.minecraft.util.math.*;
import net.minecraft.world.*;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;


public class ShopSign {
    public BlockPos blockPos;
    public transient String[] signText = new String[4];
    public String sellerName = "";
    public String itemCode = "";
    public float itemQuantity = 0;
    public float priceBuy = 0;
    public float priceSell = 0;
    public Boolean canBuy = false;
    public Boolean canSell = false;
    public ShopSign(BlockPos blockPos, String[] signText) {
        //GetShopSigns.LOGGER.info("shopsign init");
        this.setBlockPos(blockPos);
        this.setSignText(signText);
    }
    public String toString(){
        if (this.sellerName == "") {
            return "";
            //String.join("\n",this.signText);
        }
        List<String> lines = new ArrayList<String>();

        lines.add(String.format("blockPos: [%d, %d, %d]",this.blockPos.getX(),this.blockPos.getY(),this.blockPos.getZ()));
        lines.add(String.format("sellerName: \"%s\"", this.sellerName));
        lines.add(String.format("itemCode: \"%s\"", this.itemCode));
        lines.add(String.format("itemQuantity: " + this.itemQuantity));
        lines.add(String.format("priceBuy: " + this.priceBuy));
        lines.add(String.format("priceSell: " + this.priceSell));
        lines.add(String.format("canBuy: " + this.canBuy));
        lines.add(String.format("canSell: " + this.canSell));
        return String.join(", ", lines);
        //return signText.toString();
    }

    public void setBlockPos(BlockPos blockPos) {
        this.blockPos = blockPos;
    }
    public void setSignText(String[] signText) {
        if (this.signText != null && Arrays.deepEquals(this.signText, signText)) {
            //GetShopSigns.LOGGER.info("bailing out, text unchanged");
            return;
        }
        this.signText = signText;
        String regex = "(B ([0-9.]+))?[ ]*(:)?[ ]*(([0-9.]+) S)?";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(signText[2]);
        if (!matcher.matches()) {
            //GetShopSigns.LOGGER.info("no matches");
            return;
        }
        if (matcher.group(2) == null && matcher.group(5) == null) {
            //GetShopSigns.LOGGER.info("Price line not formatted as shop");
            return;
        }
        this.setSellerName(signText[0]);
        this.setItemQuantity(Float.parseFloat(signText[1]));
        this.setItemCode(signText[3]);

        if (matcher.group(2) == null)
            this.setPriceBuy(0.0f);
        else
            this.setPriceBuy(Float.parseFloat(matcher.group(2)));
        this.setCanBuy((matcher.group(2) != null));

        if (matcher.group(5) == null)
            this.setPriceSell(0.0f);
        else
            this.setPriceSell(Float.parseFloat(matcher.group(5)));
        this.setCanSell(matcher.group(5) != null);
    }
    public void setSellerName(String sellerName) {
        this.sellerName = sellerName;
    }
    public void setItemCode(String itemCode) {
        this.itemCode = itemCode;
    }
    public void setItemQuantity(float itemQuantity) {
        this.itemQuantity = itemQuantity;
    }
    public void setPriceBuy(float priceBuy) {
        this.priceBuy = priceBuy;
    }
    public void setPriceSell(float priceSell) {
        this.priceSell = priceSell;
    }
    public void setCanBuy(Boolean canBuy) {
        this.canBuy = canBuy;
    }
    public void setCanSell(Boolean canSell) {
        this.canSell = canSell;
    }

}