package biz.jack.euchre


import cards.Cards._
import util.BenPredef._

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
  def playCard : Card
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
  def +(r : RoundScore) : GameScore = {
    val i = for ((t,s) <- scores) yield {
      if (t == r.team) (t,s+r.score) else (t,s)
    }
    GameScore(Map() ++ i)
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
      def preMainRound : (List[TeamPlayerHand],Contract) = {
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
        (hands.dropRight(1):::TeamPlayerHand(dealer.team, dealer.player, newDealerHand)::Nil,contract)
      }
      val (hands,contract) = preMainRound
      
      val gp = g + playMainRound(hands, new EuchreJudge(contract.trump))
      if (gp.isOver) {
        gp
      } else {
        playRound(players, gp)
      }
    }
    
    val p1::p2::p3::p4::Nil = for (t <- teams; p <- t.players) yield TeamPlayer(t,p)
    playRound(rotatePlayers(p1::p3::p2::p4::Nil, randy.nextInt(4)), 
              GameScore(Map() ++ (for (t <- teams) yield (t,0))))
  }
  
  def dealCards(players : List[TeamPlayer]) : (List[TeamPlayerHand], List[Card]) = {
    val deck = shuffle(EuchreDeck, randy)
    def deal(deck : List[Card], players : List[TeamPlayer]) : List[(Option[TeamPlayer], List[Card])] = {
      if (players == Nil) {
        (None, deck)::Nil
      } else {
        (Some(players.head), deck.take(5))::deal(deck.drop(5),players.tail)
      }
    }
    val t = deal(deck, players)
    val hands = t.flatMap{x =>
      x match {
        case (Some(TeamPlayer(t,p)), hand) =>
          List(TeamPlayerHand(t,p,hand))
        case _ =>
          Nil
      }
    }
    val extra = t.flatMap{ x=>
      x match {
        case (None,deck) => List(deck)
        case _ => Nil
      }
    }.head
    (hands,extra)
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
      case Nil => error("Unexepected")
    }
    
    orderItUp(players) match {
      case Some(TeamPlayerHand(t,p,h)) => Contract(topCard.suit, t)
      case None => nameIt(players)
    }
    
  }
  
  def playMainRound(hands : List[TeamPlayerHand], judge : EuchreJudge) : RoundScore = {
    def playTrick(hands : List[TeamPlayerHand]) : List[Team] = {
      if (hands == Nil) {
        Nil
      } else {
        def rotateToPlayer(hands : List[TeamPlayerHand], player : Player) : List[TeamPlayerHand] = {
          if (hands.head.player == player) {
            hands 
          } else {
            rotateToPlayer(hands.tail:::hands.head::Nil,player)
          }
        }
        val cards = for (TeamPlayerHand(t,p,h) <- hands) yield {
          (t,p,p.playCard)
        }
        val winningCard = judge.getWinner(cards.map(_._3))
        val winningTPC = cards.find(_._2==winningCard).get
        winningTPC._1::playTrick(rotateToPlayer(hands, winningTPC._2))
      }
    }
    
    RoundScore(null,0)
  }
  
  def scoreRound {
    
  }
  
}

