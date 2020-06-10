package com.iquantex.account.repository;

import com.iquantex.account.model.AccountStore;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface AccountStoreRepository extends CrudRepository<AccountStore, String> {

}
