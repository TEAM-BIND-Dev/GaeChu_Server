package com.teambind.gaechuserver.repository;

import com.teambind.gaechuserver.entity.LikeAccount;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

public interface LikeAccountRepository extends CrudRepository<LikeAccount, String> {
	
	@Modifying
	@Transactional
	@Query(value = "INSERT INTO like_account (reference_id, account, version) VALUES (:ref, 1, 0) " +
			"ON DUPLICATE KEY UPDATE account = account + 1", nativeQuery = true)
	int upsertIncrementAccount(String ref);
	
	@Modifying
	@Transactional
	@Query(value = "UPDATE like_account SET account = account - 1 WHERE reference_id = :ref AND account > 0", nativeQuery = true)
	int decrementAccountIfPositive(String ref);
}
