package com.jballou.getshopsigns;

import java.util.regex.*;
import java.util.*;
import net.minecraft.util.math.*;


public class ShopSign {
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

    public ShopSign(BlockPos blockPos, String[] signText) {
        //GetShopSigns.LOGGER.info("shopsign init");
        this.posHashCode = blockPos.hashCode();
        this.posString = String.format("%d %d %d", blockPos.getX(), blockPos.getY(), blockPos.getZ());
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
        this.x = blockPos.getX();
        this.y = blockPos.getY();
        this.z = blockPos.getZ();
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
        this.setItemQuantity(Integer.parseInt(signText[1]));
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
    public void setItemQuantity(Integer itemQuantity) {
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

    public String getSellerName() {
        return this.sellerName;
    }
    public String getItemCode() {
        return this.itemCode;
    }
    public Integer getItemQuantity() {
        return this.itemQuantity;
    }
    public float getPriceBuy() {
        return this.priceBuy;
    }
    public float getPriceSell() {
        return this.priceSell;
    }
    public Boolean getCanBuy() {
        return this.canBuy;
    }
    public Boolean getCanSell() {
        return this.canSell;
    }

}