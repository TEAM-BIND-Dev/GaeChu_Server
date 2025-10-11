package com.teambind.gaechuserver.repository;

import com.teambind.gaechuserver.entity.Ids.LikeAccountId;
import com.teambind.gaechuserver.entity.LikeAccount;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;


public interface LikeAccountRepository extends CrudRepository<LikeAccount, LikeAccountId> {
	
	@Modifying
	@Transactional
	@Query(value = "INSERT INTO like_account (category_id, reference_id, account, version) VALUES (:categoryId, :ref, 1, 0) " +
			"ON DUPLICATE KEY UPDATE account = account + 1", nativeQuery = true)
	int upsertIncrementAccount(int categoryId, String ref);
	
	@Modifying
	@Transactional
	@Query(value = "UPDATE like_account SET account = account - 1 WHERE category_id = :categoryId AND reference_id = :ref AND account > 0", nativeQuery = true)
	int decrementAccountIfPositive(int categoryId, String ref);
	
	Optional<LikeAccount> findByLikeAccountIdReferenceIdAndLikeAccountIdCategoryId(String referenceId, Integer categoryId);
}

