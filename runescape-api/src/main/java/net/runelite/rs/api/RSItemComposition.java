package net.runelite.rs.api;

import net.runelite.api.ItemComposition;
import net.runelite.api.IterableHashTable;
import net.runelite.mapping.Import;

public interface RSItemComposition extends ItemComposition
{
	@Import("name")
	@Override
	String getName();

	@Import("name")
	@Override
	void setName(String name);

	@Import("id")
	@Override
	int getId();

	@Import("noteTemplate")
	@Override
	int getNote();

	@Import("note")
	@Override
	int getLinkedNoteId();

	@Import("placeholder")
	@Override
	int getPlaceholderId();

	@Import("placeholderTemplate")
	@Override
	int getPlaceholderTemplateId();

	@Import("price")
	@Override
	int getPrice();

	@Import("isMembersOnly")
	@Override
	boolean isMembers();

	@Import("isTradable")
	@Override
	boolean isTradeable();

	@Import("isTradable")
	@Override
	void setTradeable(boolean yes);

	/**
	 * You probably want {@link #isStackable}
	 * <p>
	 * This is the <b>{@code int}</b> that client code uses internally to represent this true/false value. It appears to only ever be set to 1 or 0
	 * @return 0 when this type of item isn't stackable, 1 otherwise
	 */
	@Import("isStackable")
	int getIsStackable();

	@Import("maleModel")
	int getMaleModel();

	@Import("inventoryActions")
	@Override
	String[] getInventoryActions();

	@Import("groundActions")
	@Override
	String[] getGroundActions();

	@Import("getShiftClickIndex")
	@Override
	int getShiftClickActionIndex();

	@Import("getModel")
	RSModel getModel(int quantity);

	@Import("unnotedId")
	int getInventoryModel();

	@Import("recolorTo")
	short[] getColorToReplaceWith();

	@Import("retextureTo")
	short[] getTextureToReplaceWith();

	@Import("params")
	RSIterableNodeHashTable getParams();

	@Import("params")
	void setParams(IterableHashTable params);

	@Import("params")
	void setParams(RSIterableNodeHashTable params);

	@Import("decodeNext")
	void decodeNext(RSBuffer var1, int var2);
}
