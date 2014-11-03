import com.wcohen.ss.ScaledLevenstein;
import org.getalp.util.CommonSubsequences;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("All")
public class UtilTest {

    @Test
    public void testFLCSSSChars() throws Exception {
        List<String> a = new ArrayList<>();
        List<String> b = new ArrayList<>();
        List<Double> res;


        System.out.println("--FLCSS ([h, y, a, b, c, e, f] , [e, f, a, b, c, a, y, h])--");
        a.add("h");
        a.add("y");
        a.add("a");
        a.add("b");
        a.add("c");
        a.add("e");
        a.add("f");

        b.add("e");
        b.add("f");
        b.add("a");
        b.add("b");
        b.add("c");
        b.add("a");
        b.add("y");
        b.add("h");

        CommonSubsequences cs = new CommonSubsequences(a, b);
        cs.setFuzzyDistance(new ScaledLevenstein(), .5d);
        res = cs.computeSubSequenceLengths();
        System.out.print("Result=" + res + "....");
        assert (res.get(0) - 2.0 < 0.001d);
        assert (res.get(1) - 3.0 < 0.001d);
        assert (res.get(2) - 1.0 < 0.001d);
        assert (res.get(3) - 1.0 < 0.001d);
        System.out.println("Pass!");
        a.clear();
        b.clear();
        System.out.println("---------------------------------------------------------");
    }

    @Test
    public void testFLCSSSWordExact() throws Exception {
        List<String> a = new ArrayList<>();
        List<String> b = new ArrayList<>();
        List<Double> res;


        System.out.println("--FLCSS ([the, cat, is, brown, and, very, soft] , [the, brown, and, very, soft, cat])--");
        a.add("a"); //the
        a.add("b"); //cat
        a.add("c"); //is
        a.add("d");//brown
        a.add("e");//and
        a.add("f");//very
        a.add("g");//soft

        b.add("a"); //the
        b.add("d"); //brown
        b.add("e"); //and
        b.add("f"); //very
        b.add("g");//soft
        b.add("b");//cat

        CommonSubsequences cs = new CommonSubsequences(a, b);
        cs.setFuzzyDistance(new ScaledLevenstein(), .5d);
        res = cs.computeSubSequenceLengths();
        System.out.print("Result=" + res + "....");
        assert (res.get(0) - 4.0 < 0.001d);
        assert (res.get(1) - 1.0 < 0.001d);
        System.out.println("Pass!");
    }

    @Test
    public void testSingleA() throws Exception {
        List<String> a = new ArrayList<>();
        List<String> b = new ArrayList<>();
        List<Double> res;


        System.out.println("--FLCSS ([e] , [e, f, a])--");
        a.add("e");

        b.add("e");
        b.add("f");
        b.add("a");

        CommonSubsequences cs = new CommonSubsequences(a, b);
        cs.setFuzzyDistance(new ScaledLevenstein(), .5d);
        res = cs.computeSubSequenceLengths();

        System.out.print("Result=" + res + "....");
        assert (res.get(0) - 1.0 < 0.001d);
        System.out.println("Pass!");
        a.clear();
        b.clear();
        System.out.println("---------------------------------------------------------");
    }

    @Test
    public void testSingleB() throws Exception {
        List<String> a = new ArrayList<>();
        List<String> b = new ArrayList<>();
        List<Double> res;


        System.out.println("--FLCSS ([e, f, a], [e])--");
        b.add("e");

        a.add("e");
        a.add("f");
        a.add("a");


        CommonSubsequences cs = new CommonSubsequences(a, b);
        cs.setFuzzyDistance(new ScaledLevenstein(), .5d);
        res = cs.computeSubSequenceLengths();

        System.out.print("Result=" + res + "....");
        assert (res.get(0) - 1.0 < 0.001d);
        System.out.println("Pass!");
        a.clear();
        b.clear();
        System.out.println("---------------------------------------------------------");
    }

}
