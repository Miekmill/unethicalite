package net.runelite.rs.api;

import net.runelite.api.Nameable;
import net.runelite.api.NameableContainer;
import net.runelite.mapping.Import;

import java.util.HashMap;

public interface RSUserList<T extends Nameable> extends NameableContainer<T>
{
	@Import("array")
	Nameable[] getNameables();

	@Import("getSize")
	int getCount();

	@Import("capacity")
	int getSize();

	@Import("getByUsername")
	T findByName(RSUsername name);

	/**
	 * Method called by the container when an element is added
	 * @param name
	 * @param prevName
	 */
	void rl$add(RSUsername name, RSUsername prevName);

	/**
	 * Method called by the container when an element is removed
	 * @param nameable
	 */
	void rl$remove(RSUser nameable);

	@Import("capacity")
	void setCapacity(int var1);

	@Import("array")
	void setNameables(Nameable[] arr);

	@Import("newTypedArray")
	Nameable[] newContainer(int var1);

	@Import("usernamesMap")
	void setUsernamesMap(HashMap map);

	@Import("previousUsernamesMap")
	void setPreviousUsernamesMap(HashMap map);
}
