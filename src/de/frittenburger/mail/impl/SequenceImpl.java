package de.frittenburger.mail.impl;
/*
 * Copyright (c) 2018 Dirk Friedenberger <projekte@frittenburger.de>
 * 
 * This file is part of list.frittenburger.de project.
 *
 * list.frittenburger.de is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * list.frittenburger.de is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with MP3-Album-Art.  If not, see <http://www.gnu.org/licenses/>.
 * 
 */
import de.frittenburger.mail.bo.Range;
import de.frittenburger.mail.interfaces.Sequence;

public class SequenceImpl implements Sequence {

	private int to;
	private int from;
	private int maxRange;

	public SequenceImpl(int cntMessages,int range) {
		this.to = cntMessages;
		this.from = 1;
		this.maxRange = range;
	}

	@Override
	public void setFrom(int index) {
		this.from = index;
	}

	@Override
	public Range next() {
		
		Range range = new Range();
		range.to = from + maxRange - 1;
		range.from = from;
		if(range.to > to)
			range.to = to;
		
		from = range.to+1;
		
		return range;
	}

	@Override
	public boolean hasNext() {
		return from <= to;
	}

	@Override
	public String toString() {
		return "SequenceImpl [from=" + from + ", to=" + to + "]";
	}

	@Override
	public int offset() {
		return from -1;
	}

	@Override
	public int range() {
		return to - from + 1;
	}

	
}
