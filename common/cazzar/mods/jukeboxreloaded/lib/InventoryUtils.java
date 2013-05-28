package cazzar.mods.jukeboxreloaded.lib;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagShort;

public class InventoryUtils {

    public static NBTTagList writeItemStacksToTag(ItemStack[] items)
    {
        return writeItemStacksToTag(items, 64);
    }
    
    public static NBTTagList writeItemStacksToTag(ItemStack[] items, int maxQuantity)
    {
        NBTTagList tagList = new NBTTagList();
        for(int i = 0; i < items.length; i++)
        {
            if (items[i] != null)
            {
                NBTTagCompound tag = new NBTTagCompound();
                tag.setShort("Slot", (short) i);
                items[i].writeToNBT(tag);
                
                if(maxQuantity > Short.MAX_VALUE)
                    tag.setInteger("Quantity", items[i].stackSize);
                else if(maxQuantity > Byte.MAX_VALUE)
                    tag.setShort("Quantity", (short) items[i].stackSize);
                
                tagList.appendTag(tag);
            }
        }
        return tagList;
    }
    
    public static void readItemStacksFromTag(ItemStack[] items, NBTTagList tagList)
    {
        for(int i = 0; i < tagList.tagCount(); i++)
        {
            NBTTagCompound tag = (NBTTagCompound) tagList.tagAt(i);
            int b = tag.getShort("Slot");
            items[b] = ItemStack.loadItemStackFromNBT(tag);
            if(tag.hasKey("Quantity"))
            {
                NBTBase qtag = tag.getTag("Quantity");
                if(qtag instanceof NBTTagInt)
                    items[b].stackSize = ((NBTTagInt)qtag).data;
                else if(qtag instanceof NBTTagShort)
                    items[b].stackSize = ((NBTTagShort)qtag).data;
            }
        }
    }

}