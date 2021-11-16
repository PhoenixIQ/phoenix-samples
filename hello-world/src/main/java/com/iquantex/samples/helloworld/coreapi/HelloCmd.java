package com.iquantex.samples.helloworld.coreapi;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HelloCmd implements Serializable {

	private static final long serialVersionUID = -8667685124103764667L;

	/**
	 * hello 指令id
	 */
	private String helloId;

}