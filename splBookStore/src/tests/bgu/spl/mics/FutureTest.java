
import bgu.spl.mics.Future;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

public class FutureTest {
    Future<Integer> f = new Future<>();
    Future <Integer> f2 = new Future<>();
    Future <Integer> f3 = new Future<>();
    Future <Integer> f4 = new Future<>();
    Future <Integer> f5 = new Future<>();

    @Before
    public void setUp() throws Exception {
        f.resolve(1);
        f2.resolve(2);
    }

    @Test
    public void get() {
        Integer x = 2;
        assertEquals(x,f2.get());
    }

    @Test
    public void resolve() {
        assertTrue(f.isDone());
        assertFalse(f3.isDone());
    }

    @Test
    public void isDone() {

        assertEquals(false,f3.isDone());
    }

    @Test
    public void get1() {
        Integer x = 4;
        f4.resolve(4);
        assertEquals(x,f4.get(10, TimeUnit.MILLISECONDS));
        assertEquals(null,f5.get(10,TimeUnit.MILLISECONDS));
    }
}