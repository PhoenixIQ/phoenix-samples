package com.iquantex.phoenix.samples.account.bean;

import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

/**
 * 重置 2.6.0-RC11 中 Console 的 RestTemplate 的 HttpMessageConverter. 避免 JSON 转换为 XML
 * https://www.jianshu.com/p/8be6efeb17b1
 */
@Component
public class ConsolePostBeanProcessor implements BeanPostProcessor {

	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		if (beanName.equals("phoenixConsoleRestTemplate")) {
			RestTemplate restTemplate = (RestTemplate) bean;
			// 重置 HTTP 消息转换器
			List<HttpMessageConverter<?>> messageConverters = Arrays
					.asList(new StringHttpMessageConverter(StandardCharsets.UTF_8), new FastJsonHttpMessageConverter());
			restTemplate.setMessageConverters(messageConverters);
			return bean;
		}
		return BeanPostProcessor.super.postProcessAfterInitialization(bean, beanName);
	}

}
