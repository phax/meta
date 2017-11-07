package blindsignature;

import java.math.BigInteger;
import java.security.SecureRandom;

/**
 * VotingClient is a client-side of blind signature voting module.
 *
 * @author Pawel Maniora
 */
public class VotingClient
{

  /**
   * number of candidates on voting cards
   */
  private final int candidates;

  /**
   * numbers of cards - the higher amount - the safer and slower voting is.
   */
  private final int cardamount;

  /**
   * public key encriptor. everybody who wants use it to encrypt message
   */
  private final BigInteger e;

  /**
   * modulus used in encryption/decryption. everybody who wants use it
   */
  private final BigInteger mod;

  /**
   * all cards used to vote in plain format
   */
  private BigInteger [] [] cards;

  /**
   * all blind cards
   */
  private BigInteger [] [] encodedCards;

  /**
   * set of r. One r for each card used to make blinding factor
   */
  private BigInteger [] r;

  /**
   * set of inverted r used to remove blinding factor
   */
  private BigInteger [] rprim;

  /**
   * inverted r used to remove blinding factor from signed card saved in another
   * place than rprim set to send full rprim set to server
   */
  private BigInteger rfinal;

  /**
   * card signed by private key of server
   */
  private BigInteger [] signedCard;

  /**
   * voting client constructor
   */
  VotingClient (final int candidates, final int cardamount, final BigInteger e, final BigInteger mod)
  {
    this.candidates = candidates;
    this.cardamount = cardamount;
    this.e = e;
    this.mod = mod;
    this.generateCards ();
    this.generateBlindingFactors ();
    this.generateUnblindingFactors ();
    this.encodeCards ();
    this.rfinal = null;
    this.signedCard = null;
  }

  /**
   * gemerate voting cards
   */
  public final void generateCards ()
  {

    cards = new BigInteger [cardamount] [candidates];

    final SecureRandom randomGenerator = new SecureRandom ();
    for (int i = 1; i <= cardamount; ++i)
    {
      // generate 9digit number
      final int randomInt = randomGenerator.nextInt (899999999) + 100000000;
      for (int j = 1; j <= candidates; ++j)
      {
        cards[i - 1][j - 1] = new BigInteger (Integer.toString (j) + Integer.toString (randomInt));
        // System.out.println("Generated : " + j+randomInt);
      }
    }

  }

  /**
   * generate Blinding factor
   */
  public final void generateBlindingFactors ()
  {

    r = new BigInteger [cardamount];

    for (int i = 1; i <= cardamount; ++i)
    {

      BigInteger rrand = null;
      BigInteger gcd = null;
      final BigInteger one = new BigInteger ("1");

      final SecureRandom random = new SecureRandom ();

      // check that gcd(r,n) = 1 && r < n && r > 1
      do
      {
        final byte [] randomBytes = new byte [124];
        random.nextBytes (randomBytes);
        rrand = new BigInteger (1, randomBytes);
        gcd = rrand.gcd (mod);
        // System.out.println("gcd: " + gcd);
        r[i - 1] = rrand;
      } while (!gcd.equals (one) || rrand.compareTo (mod) >= 0 || rrand.compareTo (one) <= 0);
    }
  }

  /**
   * create rprim from r
   */
  public final void generateUnblindingFactors ()
  {
    rprim = new BigInteger [r.length];

    for (int i = 1; i <= r.length; ++i)
    {
      rprim[i - 1] = r[i - 1].modInverse (mod);
    }
  }

  /**
   * set final r and clear this r before send vector of requested r.
   */
  public void setRfinal (final int norequest)
  {
    if (rfinal == null && norequest >= 0 && norequest <= candidates)
    {
      rfinal = rprim[norequest];
      rprim[norequest] = new BigInteger ("0");
    }
    else
      System.out.println ("ERROR: rfinal set before!");
  }

  /**
   * set signed card generated by VotingServer
   */
  public void setSignedCard (final BigInteger [] signedCard)
  {
    this.signedCard = signedCard;
  }

  /**
   * send encodedCards to VotingServer
   */
  public final BigInteger [] [] getCodedCards ()
  {
    return encodedCards;
  }

  /**
   * send rprim to VotingServer
   */
  public BigInteger [] getRprim ()
  {

    // if rfinal != null means that rfinal is set and rprim is cleaned from
    // rfinal
    if (rfinal != null)
    {
      return rprim;
    }
    else
      return null;
  }

  /**
   * encode cards to "codecards" for acceptable to send to VotingServer
   */
  public final void encodeCards ()
  {

    encodedCards = new BigInteger [cardamount] [candidates];

    for (int i = 1; i <= cardamount; ++i)
    {
      for (int j = 1; j <= candidates; ++j)
      {
        final BigInteger re = r[i - 1].modPow (e, mod);
        encodedCards[i - 1][j - 1] = cards[i - 1][j - 1].multiply (re).mod (mod);// multiply(re).mod(mod);
        // System.out.println("Generated : " + j+randomInt);
      }
    }
  }

  /**
   * remove rprim from signed card
   */
  public void decodeCard ()
  {
    for (int j = 1; j <= candidates; ++j)
    {
      signedCard[j - 1] = signedCard[j - 1].multiply (rfinal).mod (mod);
    }
  }

  /**
   * send vote to VotingServer
   *
   * @param candidate
   *        number of candidate on wchih you voting.
   */
  public BigInteger sendVote (final int candidate)
  {
    if (signedCard != null)
    {
      decodeCard ();
      return signedCard[candidate];
    }
    else
      return null;
  }

  /**
   * inform Voter if vote is accepted or not
   */
  public void isVoteAccepted (final boolean accepted)
  {
    if (accepted)
    {
      System.out.println ("GLOS ZOSTAŁ DODANY");
      // add confirm information here
    }
    else
    {
      System.out.println ("WYSTAPIL BLAD Z DODANIEM GLOSU!");
      // add reject information here
    }
  }

}
