/*
 * Copyright (c) 2022. Miguel R.
 * All rights reserved.
 */

package engine;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class UserRepository {
	final private Map<String, User> users = new ConcurrentHashMap<>();

	public User findUserByUsername(String username) {
		return users.get(username);
	}

	public void saveUser(User user) {
		users.put(user.getEmail(), user);
	}
}
