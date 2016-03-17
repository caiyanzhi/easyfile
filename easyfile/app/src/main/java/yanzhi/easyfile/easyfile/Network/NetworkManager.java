package yanzhi.easyfile.easyfile.Network;

import java.util.Comparator;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import yanzhi.easyfile.easyfile.util.HelperUtil;

/**
 * @desc Created by yanzhi on 2016-03-17.
 */
public class NetworkManager {
    private static final int MAX_LOADFILE_THREAD_CNT = 10;
    private static final int KEEP_ALIVE_TIME = 120;
    private final ThreadPoolExecutor threadPoolExecutor;
    private static final int QUEUE_INIT_LENGTH = 20;
    private static NetworkManager instance;
    private int corePoolSize;
    private int maxPoolSize;

    public NetworkManager(int corePoolSize,int maxPoolSize){
        this.corePoolSize = corePoolSize;
        this.maxPoolSize = maxPoolSize;
        threadPoolExecutor = new ThreadPoolExecutor(corePoolSize,
                maxPoolSize,
                KEEP_ALIVE_TIME,
                TimeUnit.SECONDS,
                new PriorityBlockingQueue<Runnable>(QUEUE_INIT_LENGTH,
                        new Comparator<Runnable>() {
                            @Override
                            public int compare(Runnable lhs, Runnable rhs) {
                                return ((Task) lhs).request.priorityInteger
                                        - ((Task) rhs).request.priorityInteger;
                            }
                        })
                );
    }

    public class Task implements Runnable {

        public final NetworkRequest request;

        Task(NetworkRequest request) {
            HelperUtil.validateNull(request);
            this.request = request;
        }

        @Override
        public void run() {
            NetworkUtil.httpSend(request);
        }
    }

    //默认的网络管理者
    public synchronized static NetworkManager getInstance(){
        if (instance == null) {
            instance = new NetworkManager(MAX_LOADFILE_THREAD_CNT,
                    MAX_LOADFILE_THREAD_CNT);
        }
        return instance;
    }

    //发送网络请求
    public void sendRequest(NetworkRequest request){
        Task task = new Task(request);
        this.threadPoolExecutor.execute(task);
    }

    /**
     * 释放NetworkManager线程池
     */
    public void shutdown() {
        threadPoolExecutor.shutdown();
    }

    /**
     * 立刻释放线程池里的所有任务
     */
    public void removeWorkQueueTask() {
        PriorityBlockingQueue<Runnable> queue = (PriorityBlockingQueue<Runnable>) threadPoolExecutor.getQueue();
        Runnable[] runnables = queue.toArray(new Runnable[0]);
        for (Runnable r : runnables) {
            threadPoolExecutor.remove(r);
        }
    }

    /**
     * 是否还有空闲的线程
     * @return
     */
    public boolean hasIdleThread() {
        return threadPoolExecutor.getActiveCount() < corePoolSize;
    }

}
