package cazzar.mods.jukeboxreloaded.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import cazzar.mods.jukeboxreloaded.blocks.TileJukeBox;

public class ContainerJukeBox extends Container {
    private final int PLAYER_INVENTORY_ROWS = 3;
    private final int PLAYER_INVENTORY_COLUMNS = 9;

    private final int INVENTORY_ROWS = 3;
    private final int INVENTORY_COLUMNS = 4;
    
    TileJukeBox tileJukeBox;

    public ContainerJukeBox(InventoryPlayer inventoryPlayer, TileJukeBox tile)
    {
        tileJukeBox = tile;
        int slot = 0;
        for (int col = 0; col < INVENTORY_ROWS; ++col)
        {
            for (int row = 0; row < INVENTORY_COLUMNS; ++row)
            {
                // IInventory inv, int slot, int x, int y
                addSlotToContainer(new Slot(tile, slot++, 54 + row * 18,
                        17 + col * 18));
                // offset corner * index * width to same point of slot
            }
        }

        // Add the player's inventory slots to the container
        for (int inventoryRowIndex = 0; inventoryRowIndex < PLAYER_INVENTORY_ROWS; ++inventoryRowIndex)
        {
            for (int inventoryColumnIndex = 0; inventoryColumnIndex < PLAYER_INVENTORY_COLUMNS; ++inventoryColumnIndex)
            {
                addSlotToContainer(new net.minecraft.inventory.Slot(inventoryPlayer,
                        inventoryColumnIndex + inventoryRowIndex * 9 + 9,
                        8 + inventoryColumnIndex * 18,
                        94 + inventoryRowIndex * 18));
            }
        }

        // Add the player's action bar slots to the container
        for (int actionBarSlotIndex = 0; actionBarSlotIndex < PLAYER_INVENTORY_COLUMNS; ++actionBarSlotIndex)
        {
            addSlotToContainer(new net.minecraft.inventory.Slot(inventoryPlayer,
                    actionBarSlotIndex, 8 + actionBarSlotIndex * 18, 152));
        }
    }

    @Override
    public boolean canInteractWith(EntityPlayer entityplayer)
    {
        return true;
    }
    
    @Override
    public ItemStack transferStackInSlot(EntityPlayer par1EntityPlayer, int i)
    {
        ItemStack itemstack = null;
        net.minecraft.inventory.Slot slot = (net.minecraft.inventory.Slot)inventorySlots.get(i);
        
        if(slot != null && slot.getHasStack())
        {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();
            
            int chestSlots = tileJukeBox.getSizeInventory();
            if(i < chestSlots)
            {
                if(!mergeItemStack(itemstack1, chestSlots, inventorySlots.size(), true))
                {
                    return null;
                }
            } 
            else if(!mergeItemStack(itemstack1, 0, chestSlots, false))
            {
                return null;
            }
            if(itemstack1.stackSize == 0)
            {
                slot.putStack(null);
            } else
            {
                slot.onSlotChanged();
            }
        }
        return itemstack;
    }
}