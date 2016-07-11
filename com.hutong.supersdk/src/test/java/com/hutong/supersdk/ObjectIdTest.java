package com.hutong.supersdk;

import junit.framework.TestCase;
import org.bson.types.ObjectId;

import java.util.concurrent.*;

public class ObjectIdTest extends TestCase {

    public void testObjectId() throws InterruptedException {

        int THREAD_SIZE = 100;
        final int NUMBER = 10000;
        final boolean PRINT_OID = true;

        ExecutorService exeService = Executors.newFixedThreadPool(THREAD_SIZE);

        final ConcurrentLinkedQueue<String> list = new ConcurrentLinkedQueue<String>();
        final ConcurrentSkipListSet<String> set = new ConcurrentSkipListSet<String>();

        for (int i = 0; i < THREAD_SIZE; i++) {
            final String name = "T" + i;
            final Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    System.out.println("Start:" + name);
                    for (int j = 0; j < NUMBER; j ++){
                        ObjectId oid = new ObjectId();
                        if (PRINT_OID) {
                            System.out.println(name + ":" + oid.toString());
                        }
                        list.add(oid.toString());
                        set.add(oid.toString());
                    }
                    System.out.println("End:" + name);
                }
            });
            exeService.execute(thread);
        }

        //结束，关闭线程池
        exeService.shutdown();

        //每10s检查线程池是否已经执行结束
        while (!exeService.awaitTermination(10, TimeUnit.SECONDS));

        System.out.println("Total Size : " + list.size());
        assert list.size() == set.size();
    }
}
