package de.frittenburger.mail.interfaces;

import java.io.IOException;

import de.frittenburger.mail.impl.FolderWrapper;

public interface EmailIndexSyncService {

	Sequence sync(Index index, FolderWrapper inbox) throws IOException;
	
}
