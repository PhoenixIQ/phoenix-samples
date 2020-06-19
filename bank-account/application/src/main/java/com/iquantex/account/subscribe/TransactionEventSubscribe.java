package com.iquantex.account.subscribe;

import com.iquantex.account.utils.JsonUtils;
import com.iquantex.phoenix.server.message.DeserializationReturn;
import com.iquantex.phoenix.server.message.PhoenixDeserializable;
import com.iquantex.samples.account.coreapi.command.AccountTransferCmd;
import com.iquantex.samples.account.coreapi.other.UpperAccountTransEvent;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class TransactionEventSubscribe implements PhoenixDeserializable {

	@Override
	public List<DeserializationReturn> deserialize(String className, byte[] bytes) {
		List<DeserializationReturn> deserializationReturns = new ArrayList<>();
		// 外部转账事件,转为内部转账cmd
		if (UpperAccountTransEvent.class.getName().equals(className)) {
			UpperAccountTransEvent upperAccountTransEvent = JsonUtils.decode(new String(bytes), className);
			AccountTransferCmd accountTransferReq = new AccountTransferCmd(upperAccountTransEvent.getInAccountCode(),
					upperAccountTransEvent.getOutAccountCode(), upperAccountTransEvent.getAmt());
			deserializationReturns
					.add(new DeserializationReturn(accountTransferReq, "bank-account/TA/BankTransferSaga", true));
		}
		return deserializationReturns;
	}

}
