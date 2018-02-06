package org.vision.github.springboot.other.jdk;

import java.lang.ref.WeakReference;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

/**
 * @author ganminghui
 * @date 2018-01-22
 * 用于源码分析ThreadLocal
 */
public class ThreadLocalV1<T> {

    /** (十进制)1640531527 */
    private final static int HASH_INCREMENT = 0x61c88647;

    private static AtomicInteger nextHashcode = new AtomicInteger();

    private static int getNextHashcode() { return nextHashcode.getAndAdd(HASH_INCREMENT); }

    private final int threadlocalHashcode = getNextHashcode();

    /** protected修饰，用于子类重写,这种设计可以学习. */
    protected T initialValue(){ return null; }

    public ThreadLocalV1(){}

    public static <S> ThreadLocalV1<S> withInitial(Supplier<? extends S> supplier){
        return new SuppliedThreadLocal<>(supplier);
    }

    /** 内部类,用于扩展父类功能设置初始值,这种设计可以学习. */
    static class SuppliedThreadLocal<T> extends ThreadLocalV1<T>{
        /** PECS原则, supplier是生产者Product,使用extends */
        private final Supplier<? extends T> supplier;

        public SuppliedThreadLocal(Supplier<? extends T> supplier){ this.supplier = Objects.requireNonNull(supplier); }

        @Override protected T initialValue() { return this.supplier.get(); }
    }


    /** 一个线程只有一个threadlocalmap,因此不会有并发问题 */
    static class ThreadLocalMap{
        /** 继承WeakReference,使得referent GC root不可达时被回收 */
        static class Entry extends WeakReference<ThreadLocalV1<?>>{
            Object value;
            public Entry(ThreadLocalV1<?> referent,Object value) {
                super(referent);
                this.value = value;
            }
        }
        /** 默认容量为16 */
        private final static int INITIAL_CAPACITY = 1<<4;
        /** 默认数据结构数组,存放Entry类型数据 */
        private Entry[] table;
        /** 默认实际占位数为0 */
        private int size = 0 ;
        /** 默认阀值为0 */
        private int threshold = 0;

        /** 设置阀值,设置为实际大小的三分之二 */
        private void setThreshOld(int len){
            this.threshold = len *2 /3 ;
        }

        /** 当前索引的下一个索引值(当前是最后一个索引值,下一个索引值为0) */
        private int nextIndex(int index,int len){ return index < len -1 ? index +1 : 0 ; }

        /** 当前索引的前一个索引值(当前是第一个索引值,前一个索引值为len-1) */
        private int preIndex(int index,int len){ return index == 0 ? len-1 : index-1; }

        /** 初始化table, 设置第一个entry, 设置实际占位数, 设置阀值 */
        ThreadLocalMap(ThreadLocalV1<?> firstKey,Object firstVal){
            table = new Entry[INITIAL_CAPACITY];
            int i = firstKey.threadlocalHashcode & (INITIAL_CAPACITY -1);
            table[i] = new Entry(firstKey,firstVal);
            size = 1;
            setThreshOld(INITIAL_CAPACITY);
        }

        /** 根据父线程的threadlocalmap来构建map */
        private ThreadLocalMap(ThreadLocalMap parentMap){
            Entry[] parentTable = parentMap.table;
            int len = parentTable.length;
            setThreshOld(len);
            table = new Entry[len];

            for (int j = 0; j < len; j++) {
                Entry entry = parentTable[j];
                if(entry!=null){
                    @SuppressWarnings("unchecked")
                    ThreadLocalV1<Object> key = (ThreadLocalV1<Object>) entry.get();
                    if(key != null){
                        /** childValue 子类重写,父类不提供功能. */
                        Object value = key.childValue(entry.value);
                        /** 根据len计算索引 */
                        int index = key.threadlocalHashcode >> (len-1);
                        /** 计算可占用的索引(这个地方不会死循环,因为table由空到满.) */
                        while (table[index]!=null){ index = nextIndex(index,len); }
                        /** table 占位 */
                        table[index] = new Entry(key,value);
                        /** 实际占位增加(不存在并发问题.) */
                        size ++;
                    }
                }
            }
        }

        /** 根据threadlocal实例获取数据 */
        private Entry getEntry(ThreadLocalV1<?> key){
            int index = key.threadlocalHashcode & (table.length -1);
            Entry entry = table[index];
            if(entry != null && entry.get() == key){
                return entry;
            }else {
                return getEntryAfterMiss(key, index, entry);
            }
        }

        /** 当没有获取到值时,可能在扩容计算没有完成,旧的索引在新的table中找不到对应的值. */
        private Entry getEntryAfterMiss(ThreadLocalV1<?> key,int index, Entry entry){
            Entry[] tmpTab = table;
            int len = tmpTab.length;

            /** 不停的轮训 */
            while (entry!=null){
                ThreadLocalV1<?> k = entry.get();
                if(k == key) { return  entry; }
                /** entry的referent不存在,可能是弱引用被回收,需要将index所在的坑位重新占位,数据清理 */
                if(k == null) { expungeStaleEntry(index);  }
                else { index = nextIndex(index,len); }
                /** entry 重新占位 */
                tmpTab[index] = entry;
            }
            /** 旧的索引肯定获取不到数据,返回null */
            return null;
        }

        /** 将index所在的坑位清理,并将由index开始的下一个索引开始重新索引 */
        private int expungeStaleEntry(int index){
            Entry[] tmpTab = table;
            int len = tmpTab.length;

            /** 回收index坑位 */
            table[index].value = null; table[index] = null; size --;

            /** 从index开始的next索引,table[nextIndex]!=null,重新生成索引 */
            Entry e;int i;
            for(i=nextIndex(index,len);(e=tmpTab[i])!=null;i=nextIndex(i,len)){
                ThreadLocalV1<?> k = e.get();
                if (k == null) {
                    /** 轮训出entry的referent不存在,直接清理掉. */
                    e.value = null; tmpTab[i] = null; size--;
                }else {
                    /** 重新生成索引   */
                    int newIndex = k.threadlocalHashcode & (len -1);
                    /** 再次检查新索引是否有效(可能存在扩容未完成,for轮训出来的索引和新索引不一样) */
                    if(newIndex!=i) { tmpTab[newIndex] = null; }
                    while (table[newIndex]!=null){ newIndex = nextIndex(newIndex,len); }

                    tmpTab[newIndex] = e;
                }
            }
            return i;
        }

        /** 设置Entry,涉及到扩容、重hash等 */
        private void setEntry(ThreadLocalV1<?> key,Object v){
            Entry[] tab = table;
            int len = tab.length;
            int index = key.threadlocalHashcode & (len -1);

            for(Entry e = tab[index]; e!=null; e = tab[nextIndex(index,len)]){
                ThreadLocalV1<?> k = e.get();
                if(k == key) { e.value = v; return; }
                /** 轮训过程中可能弱引用被回收,则重新索引 */
                if(k == null) { /** todo */ return;}
            }
            tab[index] = new Entry(key, v);
            int sz = ++size;
            //if (!cleanSomeSlots(index, sz) && sz >= threshold) { rehash(); }
        }
    }

    /** 父类不提供功能，让子类去重写实现. */
    private T childValue(T parentVal){ throw new UnsupportedOperationException(); }

    public static void main(String[] args) throws InterruptedException {
        int threshold = 1<<4 ;
        int count = 0;

        System.out.println("初始阀值:"+threshold+"(个)");

        while (count <5){
            threshold = threshold*2/3;
            count ++;
            System.out.println("第"+count+"次扩容,阀值:"+threshold+"(个)");
        }

        for (int i = 1; i <= ThreadLocalMap.INITIAL_CAPACITY ; i++) {
            System.out.println("第"+i+"次索引值:"+(getNextHashcode() & (ThreadLocalMap.INITIAL_CAPACITY-1)));
        }

    }
}