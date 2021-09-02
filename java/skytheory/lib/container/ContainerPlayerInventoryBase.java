package skytheory.lib.container;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;

public abstract class ContainerPlayerInventoryBase extends ContainerBase {

	public static final int MAIN_SIZE = 36;
	public static final int TOTAL_SIZE = 41;

	public static final int HOTBAR_X = 0;
	public static final int HOTBAR_Y = 58;
	public static final int HOTBAR_ROW = 9;
	public static final int HOTBAR_INDEX = 0;
	public static final int HOTBAR_SIZE = 9;

	public static final int INVENTORY_X = 0;
	public static final int INVENTORY_Y = 0;
	public static final int INVENTORY_ROW = 9;
	public static final int INVENTORY_INDEX = 9;
	public static final int INVENTORY_SIZE = 27;

	public static final int ARMORS_INDEX = 36;

	public static final int HEAD_X = 0;
	public static final int HEAD_Y = -76;

	public static final int CHEST_X = 0;
	public static final int CHEST_Y = -58;

	public static final int LEGS_X = 0;
	public static final int LEGS_Y = -40;

	public static final int FEET_X = 0;
	public static final int FEET_Y = -22;

	public static final int OFF_HAND_X = 69;
	public static final int OFF_HAND_Y = -22;
	public static final int OFF_HAND_INDEX = 40;

	protected final int offsetX;
	protected final int offsetY;

	public final IItemHandler playerItems;

	/**
	 * アーマー、オフハンド含むプレイヤーの全てのスロット
	 */
	public final List<SlotItemHandler> playerSlots = new ArrayList<>();
	/**
	 * プレイヤーの持つホットバーのスロット
	 */
	public final List<SlotItemHandler> playerHotBar = new ArrayList<>();
	/**
	 * ホットバーを含まないプレイヤーのインベントリのスロット
	 */
	public final List<SlotItemHandler> playerInventory = new ArrayList<>();
	/**
	 * プレイヤーのホットバー、インベントリのスロット
	 */
	public final List<SlotItemHandler> playerMainInventory = new ArrayList<>();
	/**
	 * プレイヤーのアーマースロット
	 */
	public final List<SlotItemHandler> playerArmors = new ArrayList<>();
	/**
	 * プレイヤーのオフハンドスロット
	 */
	public final List<SlotItemHandler> playerOffHand = new ArrayList<>();

	/**
	 * プレイヤーのインベントリを持つContainerを作成する
	 * スロットの位置は通常のプレイヤーインベントリと同じ
	 * @param player
	 */
	public ContainerPlayerInventoryBase(EntityPlayer player) {
		this(player, 8, 84);
	}

	/**
	 * プレイヤーのインベントリを持つContainerを作成する
	 * インベントリの最初のスロットの左上位置を引数に渡すこと
	 * @param player
	 * @param offsetX
	 * @param offsetY
	 */
	public ContainerPlayerInventoryBase(EntityPlayer player, int offsetX, int offsetY) {
		super(player);
		this.offsetX = offsetX;
		this.offsetY = offsetY;
		this.playerItems = new InvWrapper(player.inventory);
	}

	/**
	 * Container及びGUIにプレイヤーのホットバーとインベントリを追加する
	 * @param player
	 */
	public List<SlotItemHandler> addPlayerSlots() {
		List<SlotItemHandler> list = new ArrayList<SlotItemHandler>();
		list.addAll(this.addPlayerHotbar());
		list.addAll(this.addPlayerInventory());
		return list;
	}

	/**
	 * Container及びGUIにアーマーやオフハンドを含めた全てのアイテムを追加する
	 * @param player
	 */
	public List<SlotItemHandler> addPlayerAllSlots() {
		List<SlotItemHandler> list = new ArrayList<SlotItemHandler>();
		list.addAll(this.addPlayerHotbar());
		list.addAll(this.addPlayerInventory());
		list.addAll(this.addPlayerArmorSlots());
		list.addAll(this.addPlayerOffhand());
		return list;
	}

	/*
	 * ここからProtectedなメソッド
	 * プレイヤーのインベントリを追加するのに使用している
	 */

	protected List<SlotItemHandler> addSlotIterator(int posX, int posY, int row, int start, Function<Integer, Boolean> condition) {
		List<SlotItemHandler> list = new ArrayList<SlotItemHandler>();
		for (int i = start; condition.apply(i); i++) {
			list.add(this.addSlotFromInventory(this.playerItems, i, -start, posX, posY, row));
		}
		return list;
	}

	protected List<SlotItemHandler> addSlotIterator(int posX, int posY, int offset, int row, int size) {
		List<SlotItemHandler> list = this.addSlotIterator(posX, posY, row, offset, (num -> num < offset + size));
		return list;
	}

	protected List<SlotItemHandler> addPlayerHotbar() {
		List<SlotItemHandler> list = this.addSlotIterator(HOTBAR_X + offsetX, HOTBAR_Y + offsetY, HOTBAR_INDEX, HOTBAR_ROW, HOTBAR_SIZE);
		this.playerSlots.addAll(list);
		this.playerHotBar.addAll(list);
		this.playerMainInventory.addAll(list);
		return list;
	}

	protected List<SlotItemHandler> addPlayerInventory() {
		List<SlotItemHandler> list = addSlotIterator(INVENTORY_X + offsetX, INVENTORY_Y + offsetY, INVENTORY_INDEX, INVENTORY_ROW, INVENTORY_SIZE);
		this.playerSlots.addAll(list);
		this.playerInventory.addAll(list);
		this.playerMainInventory.addAll(list);
		return list;
	}

	protected List<SlotItemHandler> addPlayerArmorSlots() {
		List<SlotItemHandler> list = new ArrayList<SlotItemHandler>();
		int index = ARMORS_INDEX;
		list.add(this.createArmorSlot(FEET_X + offsetX, FEET_Y + offsetY, index++, EntityEquipmentSlot.FEET));
		list.add(this.createArmorSlot(LEGS_X + offsetX, LEGS_Y + offsetY, index++, EntityEquipmentSlot.LEGS));
		list.add(this.createArmorSlot(CHEST_X + offsetX, CHEST_Y + offsetY, index++, EntityEquipmentSlot.CHEST));
		list.add(this.createArmorSlot(HEAD_X + offsetX, HEAD_Y + offsetY, index++, EntityEquipmentSlot.HEAD));
		list.forEach(slot -> this.addSlotToContainer(slot));
		this.playerSlots.addAll(list);
		this.playerArmors.addAll(list);
		return list;
	}

	protected SlotItemHandler createArmorSlot(int posX, int posY, int index, EntityEquipmentSlot type) {
		return new SlotArmor(this.playerItems, index, posX, posY, this.player, type);
	}

	protected List<SlotItemHandler> addPlayerOffhand() {
		List<SlotItemHandler> list = new ArrayList<SlotItemHandler>();
		list.add(new SlotOffHand(this.playerItems, OFF_HAND_INDEX, OFF_HAND_X + offsetX, OFF_HAND_Y + offsetY));
		list.forEach(slot -> this.addSlotToContainer(slot));
		this.playerSlots.addAll(list);
		this.playerOffHand.addAll(list);
		return list;
	}

}
