package biz.jack.euchre


import cards.Cards._

object EuchreCore {
  val WinningScore = 10;
  val randy = new java.util.Random
}

import EuchreCore._

trait Player {
  def ordersItUp : Boolean
  def nameIt : Option[Suit]
  def forcedToNameIt : Suit
  def dropCard(card : Card) : Card
}

class EuchreScoreKeeper {
  
}

case class Team(val players : List[Player]) {
  require(players.size == 2, "Only two players per team allowed")
}

case class TeamPlayer(team : Team, player : Player) {
  
}
case class TeamPlayerHand(team : Team, player : Player, hand : List[Card]) {
  
}

case class RoundScore(val team : Team, val score : Int )

case class GameScore(val scores : Map[Team, Int]) {
  require(scores.size == 2, "Inadequate number of scores")
  def isOver : Boolean = !scores.filter(_._2 > WinningScore).isEmpty
  def +(r : RoundScore) = scores.map { x =>
    val (team,score) = x
    if (team == r.team) {
      (team,r.score + score)
    } else {
      x
    }
  }
}

case class Contract(trump : Suit, maker : Team)

class EuchreCore(val teams : List[Team]) {
  require(teams.size == 2, "Only two teams allowed")
  
  def startGame = {
    def rotatePlayers(players : List[TeamPlayer], spins:Int) : List[TeamPlayer] = {
      if (spins <= 0) {
        players
      } else {
        rotatePlayers(players.tail:::players.head::Nil, spins - 1)
      }
    }
    def playRound(players : List[TeamPlayer], g : GameScore) : GameScore = {
      def preMainRound : List[TeamPlayerHand] = {
        val (hands, deck) = dealCards(players)
        val contract = playCallRound(hands, deck.head)
      
        val replacementCard = if (contract.trump == deck.head.suit) {
          deck.head
        } else {
          deck.tail.head
        }
        val dealer = hands.last
        val removedCard = dealer.player.dropCard(replacementCard)
        val newDealerHand = replacementCard::dealer.hand.filter(removedCard!=)
        hands.dropRight(1):::TeamPlayerHand(dealer.team, dealer.player, newDealerHand)::Nil
      }
      val hands = preMainRound
      
      playMainRound(hands)
      
    }
    
    val p1::p2::p3::p4::Nil = for (t <- teams; p <- t.players) yield TeamPlayer(t,p)
    playRound(rotatePlayers(p1::p3::p2::p4::Nil, randy.nextInt(4)), 
              GameScore(Map() ++ (for (t <- teams) yield (t,0))))
  }
  
  def dealCards(players : List[TeamPlayer]) : (List[TeamPlayerHand], List[Card]) = {
  }
  
  def playCallRound(players : List[TeamPlayerHand], topCard : Card) : Contract = {
    
    def orderItUp(players : List[TeamPlayerHand]) : Option[TeamPlayerHand] = players match {
      case p::ps => if (p.player.ordersItUp) Some(p) else orderItUp(ps)
      case Nil => None
    }
    
    def nameIt(players : List[TeamPlayerHand]) : Contract = players match {
      case p::Nil => Contract(p.player.forcedToNameIt, p.team)
      case p::ps => 
        val s = p.player.nameIt
        if (s.isDefined) {
          Contract(s.get, p.team)
        } else {
          nameIt(ps)
        }
    }
    
    orderItUp(players) match {
      case Some(TeamPlayerHand(t,p,h)) => Contract(topCard.suit, t)
      case None => nameIt(players)
    }
    
  }
  
  def playMainRound(hands : List[TeamPlayerHand]) {
    
  }
  
  def scoreRound {
    
  }
  
}

