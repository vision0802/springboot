package org.vision.github.springboot.tools.idgenerator.snowflake;

import java.util.HashMap;
import java.util.Map;

/**
 * @author ganminghui
 * @date 2018/1/5
 * @see <a href="http://blog.csdn.net/li396864285/article/details/54668031"/>
 */
public class SnowFlakeFactory {
    private static final long TWEPOCH = 1288834974657L;
    private static final long WORKERID_BITS = 5L;
    private static final long DATACENTERID_BITS = 5L;
    private static final long SEQUENCE_BITS = 12L;
    private static final long TIMESTAMP_BITS = 41L;

    private static final long MAX_WORKERID = ~(-1L << WORKERID_BITS);
    private static final long MAX_DATACENTERID = ~(-1L << DATACENTERID_BITS);
    private static final long MAX_SEQUENCEID = ~(-1L << SEQUENCE_BITS);
    private static final long MAX_TIMESTAMP = ~(-1L << TIMESTAMP_BITS);

    /** 生产者二进制偏移量 */
    private static final long WORKERID_SHIFT = SEQUENCE_BITS;
    /** 数据中心二进制偏移量 */
    private static final long DATACENTERID_SHIFT = WORKERID_BITS + WORKERID_SHIFT;
    /** 时间戳二进制偏移量 */
    private static final long TIMESTAMP_SHIFT = DATACENTERID_BITS + DATACENTERID_SHIFT;

    /** long 64位
     *  0 (1位 最高位不使用)
     *  0 00000000 00000000 00000000 00000000(41位 时间戳)为了保证41位,将时间减去常量TWEPOCH
     *  00000 (5位 数据中心5位)
     *  00000 (5位 生产者5位)
     *  000000000000(12位 自增序列)
     * */

    private long workId;
    private long datacenterId;
    private long lastTimestamp = -1L;
    private long sequence = 0L;

    public SnowFlakeFactory(long workId,long datacenterId){
        if(workId < 0 || workId > MAX_WORKERID){
            throw new IllegalArgumentException(String.format("worker id can't be greater than %d or less than 0",MAX_WORKERID));
        }

        if(datacenterId <0 || datacenterId > MAX_DATACENTERID){
            throw new IllegalArgumentException(String.format("datacenter id can't be greater than %d,or less than 0",MAX_DATACENTERID));
        }

        this.workId = workId;
        this.datacenterId = datacenterId;
    }

    protected long timeGenerate(){ return System.currentTimeMillis(); }

    /** 如果参数小于当前时间,返回当前时间
     *  如果参数不小于当前时间,返回比参数值最接近的时间
     * */
    protected long tillNextMills(long lastTimestamp){
        long timestamp = timeGenerate();
        while (lastTimestamp > timestamp){
            timestamp = timeGenerate();
        }
        return timestamp;
    }

    public synchronized long nextId(){
        long timestamp = timeGenerate();

        if(timestamp < lastTimestamp){
            throw new RuntimeException(String.format("Clock moved backwards. Refusing to generate id for %d milliseconds.",lastTimestamp - timestamp));
        }
        /** 同一毫秒sequence递增直到MAX_SEQUENCEID,重新生成新的毫秒 */
        if(lastTimestamp == timestamp){
            sequence = (sequence+1) & MAX_SEQUENCEID ;
            if(sequence == 0L){
                timestamp = tillNextMills(timestamp);
            }
        }else {
            sequence = 0L;
        }

        lastTimestamp = timestamp;

        return generateId(sequence, workId, datacenterId, timestamp);
        //return ((timestamp - TWEPOCH) << TIMESTAMP_SHIFT) | (datacenterId << DATACENTERID_SHIFT) | (workId << WORKERID_SHIFT) | sequence ;
    }

    private long generateId(long sequence,long worker, long datacenter, long timestamp){
        long id = 0L;

        id |= sequence;

        id |= worker << WORKERID_SHIFT;

        id |= datacenter << DATACENTERID_SHIFT;

        id |= (timestamp- TWEPOCH) << TIMESTAMP_SHIFT;

        return id;
    }

    public static Map<String,Long> converId(long id){
        Map<String,Long> rtnMap = new HashMap<>(4);

        rtnMap.put("seq",id & MAX_SEQUENCEID);
        rtnMap.put("workid",id >>> WORKERID_SHIFT & MAX_WORKERID );
        rtnMap.put("dataid",id>>>DATACENTERID_SHIFT & MAX_DATACENTERID);
        rtnMap.put("timestamp",(id>>>TIMESTAMP_SHIFT & MAX_TIMESTAMP)+TWEPOCH);
        return rtnMap;
    }
}