package org.vision.github.springboot.tools.schedule.base;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.vision.github.springboot.tools.schedule.lock.Lockable;

/**
 * @author ganminghui
 * 抽象job类
 */
@Slf4j public abstract class BaseJob<K, C extends BaseJob.BaseJobRuntimeContext> {
    protected BaseJob(Lockable lockable){ this.lockable = lockable; }

    /** 干活带把锁, 一个活一把锁 */
    protected Lockable lockable;

    /** 获取加锁标识 @return */
    protected String getLockKey(){ return this.getClass().getSimpleName(); }

    /** 获取阀值 @return */
    public int threshold(){ return -1; }

    /** 钩子方法, 干活之前做点啥 @return */
    protected abstract boolean beforeAction();

    /** 钩子方法，活干完了还是要收尾滴 */
    protected abstract void afterAction();

    /** 钩子方法, 创建job运行时上下文对象 @return */
    protected abstract C createJobRuntimeContext();

    /**
     * 钩子方法, 是否继续
     * @param context 运行时上下文对象
     * @return true or false
     */
    protected abstract boolean isContinue(C context);

    /**
     *  钩子方法, 获取数据
     *  @param context 运行时上下文对象
     *  @return K 数据对象
     */
    protected abstract K fetchItem(C context);

    /**
     * 钩子方法, 处理数据核心方法
     * @param item 待处理的数据
     */
    public abstract void processItem(K item);

    /** 模版方法, 干活步骤, 我说怎么走, 你就怎么走 */
    public final void execute() throws Exception {
        String lockKey = getLockKey();
        lockable.tryLock(lockKey);
        log.info("对lockKey:【{}】加锁成功, 锁标识:【{}】!!!");

        try{
            doExecute();
        }catch (Exception e){
            log.error("the job named {} execute fail,error：", this.getClass().getSimpleName(),e);
        }finally { /** finally不要有return(会将捕获的异常吃掉) */
            lockable.releaseLock(lockKey);
            log.info("对lockKey:【{}】解锁成功!!!");
        }
    }

    /** 封装实际干活的方法 */
    private void doExecute() {
        if(!beforeAction()){ return ; }

        C context = createJobRuntimeContext();
        K item;
        while (isContinue(context)){
            item = fetchItem(context);
            try{
                processItem(item);
                context.proccesedRecds += context.currentFetchedRecords;
            }catch (Exception e){
                context.failedRecds += context.currentFetchedRecords;
                log.error("the job named {} doExecute fail,error message is {}", this.getClass().getSimpleName(),e);
            } finally {
                afterAction();
            }
        }
    }

    /** 抽象job运行时上下文 */
    @NoArgsConstructor(access = AccessLevel.PROTECTED) protected abstract static class BaseJobRuntimeContext {
        /** 是否溢出标识      */protected boolean isAdvancedOver = false;
        /** 所有处理的总数量   */protected long fetchedRecords = 0;
        /** 处理成功的总数量   */protected long proccesedRecds = 0;
        /** 处理失败的总数量   */protected long failedRecds = 0;
        /** 当前抓取数据的数量 */protected long currentFetchedRecords = 0;
    }
}
