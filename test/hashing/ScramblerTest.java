package hashing;

import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;

@RunWith(Theories.class)
public class ScramblerTest {
    @DataPoints
    public static final Scrambler[] scramblers = Scramblers.values();

    @Theory
    public void testIdempotency(Scrambler s) {
        for (int x = 0; x < 453; x++)
            assertEquals(s.scramble(x), s.scramble(x));
    }
}