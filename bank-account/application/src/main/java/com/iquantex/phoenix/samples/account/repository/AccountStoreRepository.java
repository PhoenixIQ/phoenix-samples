package com.iquantex.phoenix.samples.account.repository;

import com.iquantex.phoenix.samples.account.model.AccountStore;
import org.springframework.data.repository.CrudRepository;

public interface AccountStoreRepository extends CrudRepository<AccountStore, String> {

}
