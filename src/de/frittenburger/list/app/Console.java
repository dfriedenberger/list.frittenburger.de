package de.frittenburger.list.app;
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

import de.frittenburger.list.crypt.DataProtector;
import de.frittenburger.list.crypt.FileSecretProvider;
import de.frittenburger.list.crypt.HashCalculator;
import de.frittenburger.list.interfaces.Configuration;

public class Console {

	public static void main(String[] args) throws Exception {

		if(args.length == 2)
			switch(args[0])
			{
				case "-hash":
					HashCalculator hasher = new HashCalculator();
					String salt = hasher.genSalt(8);
					System.out.println("passwordsalt = "+salt);
					System.out.println("passwordhash = "+hasher.hash(salt,args[1]));
					System.exit(0);
				case "-protect":
					DataProtector protector = new DataProtector(new FileSecretProvider(Configuration.secret));
					System.out.println("password   = "+protector.protect(args[1]));
					System.exit(0);
				
			}
		System.out.println("-hash password - generate salt and hash");
		System.out.println("-protect password - generate crypted data");
		
		
	}

}
