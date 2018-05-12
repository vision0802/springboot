package org.vision.github.springboot.tools.schedule.base;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.vision.github.springboot.tools.schedule.lock.Lockable;

import java.util.Collection;
import java.util.Objects;

/** @author ganminghui */

@Slf4j public abstract class BaseMinMaxIDScanCollectionJob<M ,K extends Collection<M>,C extends BaseMinMaxIDScanCollectionJob.MinMaxIdScanBaseJobContext> extends BaseJob<K,C> {
    public static final Integer FETCH_SIZE = 1000;

    protected BaseMinMaxIDScanCollectionJob(Lockable lockable) {
        super(lockable);
    }

    @Override protected K fetchItem(C context) {
        K item;
        if(context.currentIndex + FETCH_SIZE > context.endIndex){
            item = fetch(context.currentIndex,context.endIndex+1);
            context.currentIndex = context.endIndex+1;
        }else {
            item = fetch(context.currentIndex,context.currentIndex+FETCH_SIZE);
            context.currentIndex = context.currentIndex + FETCH_SIZE;
        }
        if(Objects.nonNull(item)){
            context.fetchedRecords += item.size();
            context.currentFetchedRecords = item.size();
        }else{
            context.currentFetchedRecords = 0;
        }
        return item;
    }

    @Override protected C createJobRuntimeContext() {
        MinMaxIdDTO dto = this.range();
        return Objects.isNull(dto) ?
                  (C) new MinMaxIdScanBaseJobContext(0,0,0,FETCH_SIZE)
                : (C)new MinMaxIdScanBaseJobContext(dto.getMinId(),dto.getMaxId(),dto.getMinId(),FETCH_SIZE);
    }

    @Override protected boolean isContinue(C context) {
        if(this.threshold() > 0 && context.fetchedRecords >= this.threshold()){
            context.isAdvancedOver = true;
            return false;
        }
        return context.currentIndex <= context.endIndex;
    }

    /** 钩子方法, 定义范围dto  @return */
    protected abstract MinMaxIdDTO range();

    /**
     * 钩子方法
     * @param begin
     * @param end
     * @return K 获取的数据(集合)
     */
    protected abstract K fetch(long begin, long end);

    /** 抽象job运行时上下文 */
    @AllArgsConstructor protected static class MinMaxIdScanBaseJobContext extends BaseJobRuntimeContext {
        /** 开始索引 */protected long startIndex;
        /** 结束索引 */protected long endIndex;
        /** 当前索引 */protected long currentIndex;
        /** 步 进 值 */protected long step;
    }

    @Data @AllArgsConstructor public static class MinMaxIdDTO{
        private Long minId;
        private Long maxId;
    }
}