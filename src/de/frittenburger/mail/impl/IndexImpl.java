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
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import de.frittenburger.mail.interfaces.Index;


public class IndexImpl implements Index {

	private Map<String,Integer> keys = new HashMap<String,Integer>();
	private Map<Integer,String> indexes = new HashMap<Integer,String>();
	private int max = 0;

	@Override
	public void register(int index, String key) {
		if(key != null)
			keys.put(key,index);
		indexes.put(index,key != null ? key : "null");
		if(index > max)
			max = recalculateMax();
	}

	public Map<Integer,String> getIndexes()
	{
		return indexes;
		
	}
	public int recalculateMax() {
		Integer[] arr = indexes.keySet().toArray(new Integer[0]);
		Arrays.sort(arr);
		
		int m = 0;
		for(int i = 0;i < arr.length;i++)
		{
			if((arr[i] == m + 1))
			{
				m = arr[i];
				continue;
			}
			break;
		}
		return m;
	}

	@Override
	public int getIndex(String key) {
		
		if(!keys.containsKey(key))
			return -1;
		int i = keys.get(key);
		if(i > max)
			return -1;
		return i;
	}

	@Override
	public int getUpperMost() {
		return max;
	}

	@Override
	public void remove(int s,int e) {
		
		
		for(int index = s;index <= max;index++)
		{
			String key = indexes.get(index);


				//clear
				indexes.remove(index);
				keys.remove(key);


			if(index > e)
			{
				//add new
				int nindex = index - e + s - 1;
				indexes.put(nindex, key);
				keys.put(key, nindex);
			}
			
		}
		max = recalculateMax();
		
		
	}


}
