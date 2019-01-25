package com.xishi.moon;

import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.*;

public class HashCacheTest {

    @Test
    public void testInit() {
        HashCache hc = new HashCache();

        new Scanner(System.in).nextLine();
    }

    @Test
    public void testBaseFeatures() {
        String key = "kev";
        String value = "value";
        HashCache hc = new HashCache();
        hc.put(key,value);

        Assert.assertEquals(value,hc.get(key));
    }
    @Test
    public void testThreadSafe(){
        int count = 100000;
        Map<String,Object> hm = new HashMap<>();
        Map hc = new HashCache();
        Map<String,Object> chm = new ConcurrentHashMap<>();

        ExecutorService service = new ThreadPoolExecutor(16,16,1000,TimeUnit.SECONDS,new LinkedBlockingQueue<>());
        for (int i = 0; i < count; i++) {
            service.execute(new Work(hm,i));
            service.execute(new Work(hc,i));
            service.execute(new Work(chm,i));
        }
        service.shutdown();
        try {
            service.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
        }

        for (int i = 0; i < count; i++) {
            String key  = String.valueOf(i);

            if (key.equals(hm.get(key))) {
                System.out.println("hash map not safe");
            }
            Assert.assertEquals(key,hc.get(key));
            Assert.assertEquals(key,chm.get(key));
        }
    }

    class Work implements Runnable{
        private Map map;
        private String k;

        public Work(Map map, int k) {
            this.map = map;
            this.k = String.valueOf(k);
        }

        @Override
        public void run() {
            map.put(k,k);
        }
    }
}
