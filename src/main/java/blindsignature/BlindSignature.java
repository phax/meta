package blindsignature;

import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;

/**
 * Blind Signature is a class which presents how to use VotingClient and
 * VotingServer Class to do full process of voting with blind signature
 *
 * @author Pawel Maniora
 * @version 0.2, 20/01/12
 */

public class BlindSignature
{

  /**
   * number of candidates on voting cards
   */
  final static int CANDIDATES = 4;

  /**
   * numbers of cards - the higher amount - the safer and slower voting is.
   */
  final static int CARDAMOUNT = 4;

  /**
   * server-side of blind signature voting module.
   */
  public static VotingServer vs;

  /**
   * client-side of blind signature voting module.
   */
  public static VotingClient vc;

  /**
   * @param args
   *        the command line arguments
   */
  public static void main (final String [] args) throws NoSuchProviderException, InvalidKeyException, SignatureException
  {
    // TODO
    final BigInteger d = BigInteger.ZERO;
    final BigInteger e = BigInteger.ZERO;
    final BigInteger mod = BigInteger.ZERO;

    // Create VotingServer and VotingClient
    // VotingServer should be created for each voter - it keeps codedCards,
    // randomly choosen number etc.
    vs = new VotingServer (CANDIDATES, CARDAMOUNT, d, e, mod);
    vc = new VotingClient (CANDIDATES, CARDAMOUNT, e, mod);

    // send coded cards from client to server
    vs.setCodedCards (vc.getCodedCards ());

    // VotingServer draw no request parameter and send info to VotingClient
    vc.setRfinal (vs.drawNoRequest ());

    // VotingServer gets info about r' parameters and if its okay sand signed
    // card to client
    if (vs.checkCards (vc.getRprim ()))
      vc.setSignedCard (vs.signCard ());

    // after Reconnect in anonymous connection
    // send vote on candidate (2 is third in this example) to serwer. if
    // everything is okay vote is added.
    vc.isVoteAccepted (vs.checkVote (vc.sendVote (2)));
  }
}
