package cn.com.morningtec;

import org.junit.Test;

import java.util.LinkedList;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        LinkedList<String> data = new LinkedList<>();
        data.add("1");
        data.add("2");
        data.add("3");
        data.add("4");
        data.add("5");

        String pop = data.pop();
        System.out.println(pop);
        assertEquals(4, 2 + 2);
    }
}