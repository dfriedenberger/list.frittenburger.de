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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import de.frittenburger.mail.interfaces.EmailCache;
import de.frittenburger.mail.interfaces.Index;


public class PersistenceEmailCache implements EmailCache {

	private final String emailIndexFile;

	Map<String,Index> map = new HashMap<String,Index>();
	private Set<String> messageKeyCache = new HashSet<String>();
	
	public PersistenceEmailCache(String emailIndexFile) throws IOException {
		this.emailIndexFile = emailIndexFile;

		
		if(!new File(emailIndexFile).exists()) return;
		
		List<String> lines = Files.readAllLines(Paths.get(emailIndexFile), StandardCharsets.UTF_8);
		for (String line : lines) {

			List<String> list = new ArrayList<String>();
			Matcher m = Pattern.compile("([^\"]\\S*|\".+?\")\\s*").matcher(line);
			while (m.find())
				list.add(m.group(1).replaceAll("^\"|\"$", ""));
			if (list.size() != 3)
				throw new IOException("invalid index file " + line);

			String folder = list.get(0);
			int index = Integer.parseInt(list.get(1));
			String messageKey = list.get(2).equals("null") ? null : list.get(2);

			getIndex(folder).register(index, messageKey);
		}

	}
	
	public void commit() {
		
		try {
			
				PrintWriter out = new PrintWriter(new OutputStreamWriter(
					    new FileOutputStream(emailIndexFile), StandardCharsets.UTF_8), true);
			
				for(String folder : map.keySet())
				{
					Index ix = map.get(folder);
					if(ix instanceof IndexImpl)
					{
						IndexImpl index = (IndexImpl)ix;
						int max = index.recalculateMax();
						Map<Integer, String> m = index.getIndexes();
						for(int i = 1;i <= max;i++)
						{
							String line = String.format("\"%s\" %d %s", folder,i,m.containsKey(i) ? m.get(i): "null");
							out.println(line);
						}
						
					}
				}
				out.close();
			
		} catch (IOException e) {
		   e.printStackTrace();
		}
	}

	
	@Override
	public Index getIndex(String folder) {
		if(!map.containsKey(folder))
			map.put(folder, new IndexImpl());
		return map.get(folder);
	}

	
	@Override
	public void add(String messageKey) {
		messageKeyCache.add(messageKey);
	}
	
	@Override
	public boolean exists(String messageKey) {
		return messageKeyCache.contains(messageKey);
	}
	
	
	

}
