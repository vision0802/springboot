package org.vision.github.springboot.tools.idgenerator.snowflake.strategy;

import org.vision.github.springboot.tools.idgenerator.snowflake.util.IDUtils;
import org.vision.github.springboot.tools.idgenerator.snowflake.util.TimeUtil;

import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author user
 * @date 2018/1/5
 */
public class ConcreateStrategy {

    public static class GenStrategy4Syn implements IGenStrategy{
        private long lastTimestamp = 0L;
        private long sequence = 0L;

        @Override public long generateId(StrategyContext context){
            TimeUtil.timestampAndSequenceGenerate(sequence,lastTimestamp,context.getIdStructure());
            return IDUtils.generateId(context.getIdStructure());
        }
    }

    public static class GenStrategy4Lock implements IGenStrategy{
        private long lastTimestamp = 0L;
        private long sequence = 0L;
        private Lock lock = new ReentrantLock();

        @Override public long generateId(StrategyContext context){
            lock.lock();
            try{
                TimeUtil.timestampAndSequenceGenerate(sequence,lastTimestamp,context.getIdStructure());
            }finally {
                lock.unlock();
            }
            return IDUtils.generateId(context.getIdStructure());
        }
    }

    public static class GenStrategy4Atomic implements IGenStrategy{
        static class Variant { private long sequence = 0, lastTimestamp = -1; }
        private AtomicReference<Variant> variant = new AtomicReference<>(new Variant());

        @Override public long generateId(StrategyContext context){
            Variant varOld, varNew;
            long timestamp, sequence;
            while (true) {
                varOld = variant.get();
                timestamp = TimeUtil.timeGenerate();

                TimeUtil.validateTimestamp(varOld.lastTimestamp,timestamp);

                sequence = varOld.sequence;
                if (timestamp == varOld.lastTimestamp) {
                    sequence++;
                    sequence &= context.getIdStructure().getMaxSequence();
                    if (sequence == 0) {
                        timestamp = TimeUtil.tillNextMills(varOld.lastTimestamp);
                    }
                } else {
                    sequence = 0;
                }

                varNew = new Variant();
                varNew.sequence = sequence;
                varNew.lastTimestamp = timestamp;

                if (variant.compareAndSet(varOld, varNew)) {
                    context.getIdStructure().setSequence(sequence);
                    context.getIdStructure().setTimestamp(timestamp);
                    break;
                }
            }
            return IDUtils.generateId(context.getIdStructure());
        }
    }
}