package org.vision.github.springboot.tools.idgenerator.snowflake.bean;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.vision.github.springboot.tools.idgenerator.snowflake.util.TimeUtil;

/**
 * @author user
 * @date 2018/1/5
 * long 64位
 *  0 (1位 最高位不使用)
 *  0 00000000 00000000 00000000 00000000(41位 时间戳)为了保证41位,将时间减去常量TWEPOCH
 *  00000 (5位 数据中心5位)
 *  00000 (5位 生产者5位)
 *  000000000000(12位 自增序列)
 */
@NoArgsConstructor
public class IDStructure {
    public static final long TWEPOCH = 1288834974657L;

    @Getter private int sequenceBit,datacenterIdBit,wokerIdBit,timestampBit;

    @Getter @Setter private long sequence=0L,workerId,datacenterId,timestamp=0L;

    @Override public String toString() {
        return "IDStructure{" + "timestamp=" + TimeUtil.formatMillis(timestamp) + ", sequence=" + sequence + ", workerId=" + workerId + ", datacenterId=" + datacenterId + '}';
    }

    /** 构造方法,传入各个段位的位数 */
    public IDStructure(int sequenceBit, int datacenterIdBit, int wokerIdBit, int timestampBit){
        this.sequenceBit = sequenceBit; this.datacenterIdBit = datacenterIdBit;
        this.wokerIdBit = wokerIdBit;   this.timestampBit = timestampBit;
    }

    /** 构造方法,传入各个段位的位数,以及两个数据 */
    public IDStructure(long wokerId,long datacenterId,int sequenceBit, int datacenterIdBit, int wokerIdBit, int timestampBit){
        this(sequenceBit,datacenterIdBit,wokerIdBit,timestampBit);
        setWorkerId(wokerId); setDatacenterId(datacenterId);
    }

    /** 最大的sequence */
    public long getMaxSequence(){ return ~(-1L << getSequenceBit()); }

    /** 最大的datacenterId */
    public long getMaxDatacenterId(){ return ~(-1L << getDatacenterIdBit()); }

    /** 最大的wokerId */
    public long getMaxWokerId(){ return ~(-1L<<getWokerIdBit()); }

    /** 最大的timestamp */
    public long getMaxTimestamp(){ return ~(-1L<<timestampBit); }

    /** 生产者二进制偏移量 */
    public int getWokerIdShift(){ return getSequenceBit(); }

    /** 数据中心二进制偏移量 */
    public int getDatacenterIdShift(){ return getWokerIdBit() + getWokerIdShift(); }

    /** 时间戳二进制偏移量 */
    public int getTimestampShift(){ return getDatacenterIdBit() + getDatacenterIdShift(); }

}