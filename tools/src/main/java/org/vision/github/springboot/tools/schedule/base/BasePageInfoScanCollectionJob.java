package org.vision.github.springboot.tools.schedule.base;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.vision.github.springboot.tools.schedule.lock.Lockable;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/** @author ganminghui */

public abstract class BasePageInfoScanCollectionJob<M,K extends List<M>,C extends BasePageInfoScanCollectionJob.PageInfoScanBaseJobContext> extends BaseJob<K,C>{

    protected BasePageInfoScanCollectionJob(Lockable lockable) { super(lockable); }

    @Override protected boolean isContinue(C context) {
        if(this.threshold() > 0 && context.fetchedRecords >= this.threshold()){
            context.isAdvancedOver = true;
            return false;
        }
        return context.currentPageIndex < context.endPageIndex ;
    }

    @Override protected K fetchItem(C context) {
        /** pageHelper分页 */
        PageHelper.startPage(context.currentPageIndex,context.pageSize);

        PageInfo<M> item = new PageInfo<>(fetch());
        context.currentPageIndex ++;
        if(Objects.nonNull(item)){
            context.fetchedRecords += item.getSize();
            context.currentFetchedRecords = item.getSize();
            context.endPageIndex = item.getNavigateLastPage();
        }else {
            context.currentFetchedRecords = 0;
            return (K) Collections.emptyList();
        }
        return (K) item.getList();
    }

    @Override protected C createJobRuntimeContext() {
        return (C) new PageInfoScanBaseJobContext(startPageIndex(),endPageIndex(),currentPageIndex(),pageSize());
    }

    /** 钩子方法, 用于获取数据 @return */
    protected abstract List<M> fetch();

    /** 钩子方法, 开始页 @return */
    protected abstract int startPageIndex();

    /** 钩子方法, 每页显示大小 @return */
    protected abstract int pageSize();

    /** 钩子方法, 当前页索引 @return */
    protected abstract int currentPageIndex();

    /** 钩子方法, 结束页 @return */
    protected abstract int endPageIndex();

    protected static class PageInfoScanBaseJobContext extends BaseJobRuntimeContext {
        /** 开始页索引 */protected int startPageIndex;
        /** 结束页索引 */protected int endPageIndex;
        /** 当前页索引 */protected int currentPageIndex;
        /** 每页显示数量*/protected int pageSize;

        protected PageInfoScanBaseJobContext(int startPageIndex, int endPageIndex, int currentPageIndex, int pageSize){
            this.startPageIndex = startPageIndex;
            this.endPageIndex = endPageIndex;
            this.currentPageIndex = currentPageIndex;
            this.pageSize = pageSize;
        }
    }
}