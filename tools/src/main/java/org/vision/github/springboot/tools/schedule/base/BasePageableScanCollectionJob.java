package org.vision.github.springboot.tools.schedule.base;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.vision.github.springboot.tools.schedule.lock.Lockable;

import java.util.List;
import java.util.Objects;

/** @author ganminghui */
public abstract class BasePageableScanCollectionJob<M,K extends List<M>,C extends BasePageableScanCollectionJob.PageableScanBaseJobContext> extends BaseJob<K,C>{
    protected BasePageableScanCollectionJob(Lockable lockable) { super(lockable); }

    @Override protected boolean isContinue(C context) {
        if(this.threshold() > 0 && context.fetchedRecords >= this.threshold()){
            context.isAdvancedOver = true;
            return false;
        }
        return context.currentPageIndex < context.endPageIndex;
    }

    @Override protected C createJobRuntimeContext() { return (C) new PageableScanBaseJobContext(pageSize()); }

    @Override protected K fetchItem(C context) {
        PageHelper.startPage(context.currentPageIndex,context.pageSize);
        PageInfo<M> item = new PageInfo<>(fetch());

        context.currentPageIndex ++;
        if(Objects.nonNull(item)){
            context.fetchedRecords += item.getSize();
            context.currentFetchedRecords = item.getSize();
            context.endPageIndex = item.getNavigateLastPage();
        }else {
            context.currentFetchedRecords = 0;
            return null;
        }
        return (K) item.getList();
    }

    /** 钩子方法, 每页显示大小 @return */
    protected abstract int pageSize();

    /** 钩子方法, 抓取数据 @return */
    protected abstract List<M> fetch();

    protected static class PageableScanBaseJobContext extends BaseJobRuntimeContext{
        /** 开始页索引 */protected int startPageIndex;
        /** 结束页索引 */protected int endPageIndex;
        /** 当前页索引 */protected int currentPageIndex;
        /** 每页显示数量*/protected int pageSize;
        protected PageableScanBaseJobContext(int pageSize){
            this.startPageIndex = 1;
            this.pageSize = pageSize;
            this.currentPageIndex = 1;
            this.endPageIndex = 2;
        }
    }
}