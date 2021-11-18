package com.iquantex.samples.helloworld.coreapi;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HelloEvent implements Serializable {

	private static final long serialVersionUID = 4778468915465985552L;

	/**
	 * hello 指令id
	 */
	private String helloId;

}