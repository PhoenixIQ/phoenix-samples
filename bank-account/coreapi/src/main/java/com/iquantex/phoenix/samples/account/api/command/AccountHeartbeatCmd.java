package com.iquantex.phoenix.samples.account.api.command;

import com.iquantex.phoenix.core.serialization.CustomSerializer;
import com.iquantex.phoenix.core.serialization.Serializers;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** 账户心跳命令 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@CustomSerializer(serializerType = Serializers.Type.JSON)
public class AccountHeartbeatCmd implements Serializable {

	private static final long serialVersionUID = 2925700142640586327L;

	/** 划拨账户 */
	private String accountCode;

}
