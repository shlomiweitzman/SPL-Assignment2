package bgu.spl.mics;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.Assert.*;

public class FutureTest<T>{
    private Future f;

    @Before
    public void setUp() throws Exception {
        f = new Future();
    }

    @After
    public void tearDown() throws Exception {}

    @Test
    public void get() {
        f.resolve(f.get(0,SECONDS));
        assertSame(f,f.get());
    }

    @Test
    public void resolve() {
        f.resolve("result");
        assert f.get()=="result";
    }

    @Test
    public void isDone() {
        assertSame(false,f.isDone());
        f.resolve(f.get());
        assertTrue(f.isDone());
    }

    @Test
    public void get1() {
        f.resolve(f.get());
        assertSame(f,f.get(0,SECONDS));
    }
}