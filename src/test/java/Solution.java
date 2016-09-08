import java.util.BitSet;
import java.util.Scanner;

public class Solution
{
  public static void main (final String [] args)
  {
    final Scanner in = new Scanner ("4\r\n" +
                                    "5 3\r\n" +
                                    "0 0 0 0 0\r\n" +
                                    "6 5\r\n" +
                                    "0 0 0 1 1 1\r\n" +
                                    "6 3\r\n" +
                                    "0 0 1 1 1 0\r\n" +
                                    "3 1\r\n" +
                                    "0 1 0");

    final int T = Integer.parseInt (in.nextLine ());
    for (int tc = 0; tc < T; ++tc)
    {
      final int n = in.nextInt ();
      final int m = in.nextInt ();
      final boolean [] free = new boolean [n];
      for (int i = 0; i < n; ++i)
        free[i] = in.nextInt () == 0;

      final Board b = new Board (free, m);
      System.out.println (b.canWin () ? "YES" : "NO");
    }
  }

  static final class Board
  {
    private final int m_entries;
    private final boolean [] m_free;
    private final int m_jump;
    private final BitSet m_visited;

    public Board (final boolean [] free, final int m)
    {
      m_entries = free.length;
      m_free = free;
      m_jump = m;
      m_visited = new BitSet (m_entries);
      assert m_free[0];
    }

    private boolean _isFree (final int pos)
    {
      return pos >= 0 && (pos >= m_entries || (m_free[pos] && !m_visited.get (pos)));
    }

    private int _canJumpForward (final int startPos)
    {
      if (_isFree (startPos + m_jump))
        return startPos + m_jump;
      if (_isFree (startPos + 1))
        return startPos + 1;
      if (_isFree (startPos - 1))
        return startPos - 1;
      return -1;
    }

    public boolean canWin ()
    {
      int pos = 0;
      while (true)
      {
        final int oldPos = pos;
        pos = _canJumpForward (pos);
        if (pos < 0)
          return false;
        if (false)
          System.out.println ("Jumped from " + oldPos + " to " + pos);
        m_visited.set (pos);
        if (pos >= m_entries)
          return true;
      }
    }
  }
}
