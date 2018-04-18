package de.frittenburger.list.impl;
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

import java.util.Date;
import java.util.HashMap;
import java.util.Map;


import de.frittenburger.list.interfaces.Job;


public class Scheduler extends Thread {

	private Map<Job,Long> jobs = new HashMap<Job,Long>();

	public void add(Job job) {
		jobs.put(job,0L);
	}
	
	@Override
	public void run() {
		
		while(true)
		{
			long now = new Date().getTime();
			for(Job job : jobs.keySet())
			{
				if(!job.shouldRun(now, jobs.get(job))) continue;
				System.out.println("Start job "+job.getClass().getSimpleName());
				job.step();
				jobs.put(job,now);				
			}
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
	}

	



}
