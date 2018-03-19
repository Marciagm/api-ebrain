/**   
 * <p>本文件仅为内部使用，请勿外传</p>
 */
package com.ebrain.api.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** 
 * <p>
 * Description: 
 * IdWorker这个类实现，其原理结构如下，用一个0表示一位，用—分割开部分的作用：
 * 1
 * 0---0000000000 0000000000 0000000000 0000000000 0 --- 00000 ---00000 ---0000000000 00
 * 在上面的字符串中，第一位为未使用（实际上也可作为long的符号位），接下来的41位为毫秒级时间，
 * 然后5位datacenter标识位，5位机器ID（并不算标识符，实际是为线程标识），
 * 然后12位该毫秒内的当前毫秒内的计数，加起来刚好64位，为一个Long型。
 * </p>
 * 时间: 2016年7月3日 下午12:08:42
 *
 * @author peisong
 * @since v1.0.0 
 */
public class IdWorker {
	protected static final Logger logger = LoggerFactory.getLogger(IdWorker.class);  
    
    private long workerId;  
    private long datacenterId;  
    private long sequence = 0L;  
    //常数，生成此时间点后的ID
    private long twepoch = 1467520182018L;  
    //机器标识位数
    private long workerIdBits = 5L;  
  //数据中心标识位数
    private long datacenterIdBits = 5L;  
  //机器ID最大值
    private long maxWorkerId = -1L ^ (-1L << workerIdBits);  
  //数据中心ID最大值
    private long maxDatacenterId = -1L ^ (-1L << datacenterIdBits);  
  //毫秒内自增位
    private long sequenceBits = 12L;  
  //机器ID偏左移12位
    private long workerIdShift = sequenceBits;  
  //数据中心ID左移17位
    private long datacenterIdShift = sequenceBits + workerIdBits;  
  //时间毫秒左移22位
    private long timestampLeftShift = sequenceBits + workerIdBits + datacenterIdBits;  
    private long sequenceMask = -1L ^ (-1L << sequenceBits);  
  
    private long lastTimestamp = -1L;  
  
    public IdWorker(long workerId, long datacenterId) {  
        // sanity check for workerId  
        if (workerId > maxWorkerId || workerId < 0) {  
            throw new IllegalArgumentException(String.format("worker Id can't be greater than %d or less than 0", maxWorkerId));  
        }  
        if (datacenterId > maxDatacenterId || datacenterId < 0) {  
            throw new IllegalArgumentException(String.format("datacenter Id can't be greater than %d or less than 0", maxDatacenterId));  
        }  
        this.workerId = workerId;  
        this.datacenterId = datacenterId;  
        logger.info(String.format("worker starting. timestamp left shift %d, datacenter id bits %d, worker id bits %d, sequence bits %d, workerid %d", timestampLeftShift, datacenterIdBits, workerIdBits, sequenceBits, workerId));  
    }  
  
    public synchronized long nextId() {  
        long timestamp = timeGen();  
      //时间错误
        if (timestamp < lastTimestamp) {  
            logger.error(String.format("clock is moving backwards.  Rejecting requests until %d.", lastTimestamp));  
            throw new RuntimeException(String.format("Clock moved backwards.  Refusing to generate id for %d milliseconds", lastTimestamp - timestamp));  
        }  
  
        if (lastTimestamp == timestamp) {  
        	//当前毫秒内，则+1
            sequence = (sequence + 1) & sequenceMask;  
            if (sequence == 0) {  
            	//当前毫秒内计数满了，则等待下一秒
                timestamp = tilNextMillis(lastTimestamp);  
            }  
        } else {  
            sequence = 0L;  
        }  
  
        lastTimestamp = timestamp;  
      //ID偏移组合生成最终的ID，并返回ID 
        return ((timestamp - twepoch) << timestampLeftShift) | (datacenterId << datacenterIdShift) | (workerId << workerIdShift) | sequence;  
    }  
  //等待下一个毫秒的到来 
    protected long tilNextMillis(long lastTimestamp) {  
        long timestamp = timeGen();  
        while (timestamp <= lastTimestamp) {  
            timestamp = timeGen();  
        }  
        return timestamp;  
    }  
  
    protected long timeGen() {  
        return System.currentTimeMillis();  
    }

    public long convertTime(String id){
    	try{
    		long idl=Long.parseLong(id);
    		return (idl>>timestampLeftShift)+twepoch;
    	}catch(Exception e){
    		return 0;
    	}
    }
    
     public static void main(String[] args){
    	IdWorker id=new IdWorker(1,0);
    	/*long ids;
    	long start = System.currentTimeMillis();  
    	for(int i=0;i<10000000;i++){
    		ids=id.nextId();
    	}
    	long end = System.currentTimeMillis(); 
    	System.out.print(end+":"+10000000000f/(end-start));*/
    	System.out.println(Long.toString(id.nextId(), 36));
    }
}  
