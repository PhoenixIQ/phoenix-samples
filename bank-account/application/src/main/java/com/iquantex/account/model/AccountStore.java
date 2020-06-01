package com.iquantex.account.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Data
@Builder
@Table(name = "ACCOUNT_STORE")
@AllArgsConstructor
@NoArgsConstructor
public class AccountStore implements Serializable {

	@Id
	private String accountCode;

}
