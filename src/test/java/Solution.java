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

    public Board (final boolean [] free, final int m)
    {
      m_entries = free.length;
      m_free = free;
      m_jump = m;
      assert m_free[0];
    }

    private boolean _isFree (final int pos, final BitSet visited)
    {
      return pos >= 0 && (pos >= m_entries || (m_free[pos] && !visited.get (pos)));
    }

    private boolean _isWin (final int pos)
    {
      return pos >= m_entries;
    }

    private boolean _canWin (final int startPos, final BitSet visited, final int level)
    {
      String prefix = "";
      for (int i = 0; i < level; ++i)
        prefix += "  ";

      int nextPos = startPos + m_jump;
      if (_isFree (nextPos, visited))
      {
        if (_isWin (nextPos))
          return true;

        if (false)
          System.out.println (prefix + "Jumping from " + startPos + " to " + nextPos);
        final BitSet visited2 = (BitSet) visited.clone ();
        visited2.set (nextPos, true);
        if (_canWin (nextPos, visited2, level + 1))
          return true;
        // else continue
      }

      nextPos = startPos + 1;
      if (_isFree (nextPos, visited))
      {
        if (_isWin (nextPos))
          return true;

        if (false)
          System.out.println (prefix + "Jumping from " + startPos + " to " + nextPos);
        final BitSet visited2 = (BitSet) visited.clone ();
        visited2.set (nextPos, true);
        if (_canWin (nextPos, visited2, level + 1))
          return true;
        // else continue
      }

      nextPos = startPos - 1;
      if (_isFree (nextPos, visited))
      {
        if (false)
          System.out.println (prefix + "Jumping from " + startPos + " to " + nextPos);

        final BitSet visited2 = (BitSet) visited.clone ();
        visited2.set (nextPos, true);
        if (_canWin (nextPos, visited2, level + 1))
          return true;
        // else continue
      }
      return false;
    }

    public boolean canWin ()
    {
      final BitSet visited = new BitSet (m_entries);
      final int pos = 0;
      visited.set (pos, true);
      return _canWin (pos, visited, 0);
    }
  }
}
