/*
 * Copyright (c) 2022. Miguel R.
 * All rights reserved.
 */

package engine;

import org.springframework.data.repository.CrudRepository;

public interface QuestionRepository extends CrudRepository<Question, Integer> {
//	Iterable<Question> findAll(Sort id);
}
