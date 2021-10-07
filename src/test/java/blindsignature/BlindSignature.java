/*
 * Copyright (C) 2014-2021 Philip Helger (www.helger.com)
 * philip[at]helger[dot]com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package blindsignature;

import java.math.BigInteger;

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
  static VotingServer vs;

  /**
   * client-side of blind signature voting module.
   */
  static VotingClient vc;

  /**
   * @param args
   *        the command line arguments
   */
  public static void main (final String [] args)
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
