package de.frittenburger.list.interfaces;

import java.io.IOException;

public interface NotificationService {

	void sendToken(String addr, String token) throws IOException;

}
