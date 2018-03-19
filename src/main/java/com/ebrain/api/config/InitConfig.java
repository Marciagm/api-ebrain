/**   
 * <p>本文件仅为内部使用，请勿外传</p>
 */
package com.ebrain.api.config;

import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

/**
 * <p>
 * Description: TODO(这里用一句话描述这个类的作用)
 * </p>
 * 时间: 2016年12月6日 下午4:10:03
 *
 * @author peisong
 * @since v1.0.0
 */
@Component
public class InitConfig implements ApplicationListener<ApplicationEvent> {
	private static boolean isStart = false;

	@Override
	public void onApplicationEvent(ApplicationEvent event) {
		if (!isStart) {
			isStart = true;
			//SysConfig.ValidSecureCode();
		}
	}

}
