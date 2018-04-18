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
import java.io.IOException;

import de.frittenburger.mail.interfaces.EmailIndexSyncService;
import de.frittenburger.mail.interfaces.Index;
import de.frittenburger.mail.interfaces.Sequence;

public class EmailIndexSyncServiceImpl implements EmailIndexSyncService {

	private static int RANGE = 100;

	@Override
	public Sequence sync(Index emailIndex, FolderWrapper folder) throws IOException {

		Sequence seq = new SequenceImpl(folder.getCount(), RANGE);

		while (true) // Maximal 1
		{
			// Syncronize sequence
			int u = emailIndex.getUpperMost();
			if (u == 0) {
				seq.setFrom(1);
				return seq;
			}

			int s = findUpperMostSyncPoint(0,u, emailIndex, folder);
			if (s == u) {
				// Alles OK
				seq.setFrom(u + 1);
				return seq;
			}

			
			System.out.println(" synced = " + s + " upperMosted = " + u);
			
			if (s == -1)
				throw new IOException("No SyncPoint found");

			// synchronisieren
			if (s < u) {
				// Sync from s to u
				int ns = sync(s, u, emailIndex, folder);
				if (ns == -1)
					throw new IOException("No SyncPoint found");

				if (ns > s) {
					// Noch mal probieren
					continue;
				}

				if (ns == s) {
					if(s + 1000 < u)
					{
						if(s + 1000 < folder.getCount())
						{
							throw new IOException(" not plausibel s= " + s + " u= "+u);
						}
					}
					emailIndex.remove( s + 1, u);
					seq.setFrom(s + 1);
					return seq;
				}

			}

			break;

		}
		throw new IOException("Not implemented");

	}

	private int sync(int s, int u, Index emailIndex,FolderWrapper folder) throws IOException {

		// Was ist an Position s + 1
		int ix = s + 1;
		String boxMessageKey = folder.listMessage(ix);
		int i = emailIndex.getIndex(  boxMessageKey);

		if (i == -1) {
			// not found in Index, muss eine neue Email sein
			return s;
		}

		if (i > ix) {
			// Die Mails im Index zwischen ix und i - 1 existieren nicht mehr,
			// wurden gelöscht.
			// clear Index
			emailIndex.remove( ix, i - 1);
			return s + 1;
		}

		throw new IOException("sync not implemented ix = "+ix+" i = "+i);
	}

	private int findUpperMostSyncPoint(int o,int u,Index emailIndex,  FolderWrapper folder)
			throws IOException {

		String msgKey = folder.listMessage(u);
		if (msgKey != null) // Todo
		{
			int i = emailIndex.getIndex(msgKey);
			if (i == u) {
				return i;
			}
		}

		if(u == 1) //Das war das letzte Element
			return 0;
			
		if(o + 1 == u) //Da gibts nichts mehr zu finden
			return o;
		
		int no = o;
		// try
		while(true)
		{
			int nu = (no + u) / 2;
			
			if (nu <= o  || nu >= u)
				throw new IOException(" o = " + o + " u = " + u + " nu = " + nu+ " no = " + no);

			int ns = findUpperMostSyncPoint(no,nu, emailIndex,  folder);
		    if(ns == nu && no + 1 < u)
		    {
		    	//Vll gehts noch besser
		    	no = nu;
		    	continue;
		    }
		
		    return ns;
		}

	}

}
