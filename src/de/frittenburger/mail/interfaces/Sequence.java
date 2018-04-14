package de.frittenburger.mail.interfaces;

import de.frittenburger.mail.bo.Range;

public interface Sequence {

	public Range next();

	public boolean hasNext();

	public void setFrom(int index);

	public int offset();

	public int range();

}
