package biz.jack.euchre

import util.BenPredef._
import cards.Cards._

case class EuchreRules()

object EuchreGame {
  val randy = new java.util.Random
}

import EuchreGame._

trait EuchreDecider {
  def pickItUp(dealtState : EuchreDealtState, gameState : EuchreGameState) : Option[PlayerBid]
  def nameIt(dealtState : EuchreDealtState, gameState : EuchreGameState) : Option[(Suit, PlayerBid)]
  def nameItForced(dealtState : EuchreDealtState, gameState : EuchreGameState) : (Suit, PlayerBid)
}

abstract case class EuchreAction()

case class EuchreGameState(val rounds : List[EuchreRoundState])
case class EuchreRoundState(val players : List[Player])
case class EuchreDealtState(val prev : EuchreRoundState, val dealt : (List[(Player, List[Card])],List[Card])) {
  lazy val flippedCard = dealt._2.head
  lazy val hiddenCard = dealt._2.tail.head
}
case class EuchreBiddedState(val prev : EuchreDealtState, val maker : Player, val suit : Suit, val bid : PlayerBid, val dealerCard : Card) {
  lazy val wasOrderedUp = prev.flippedCard == dealerCard
}

case class PlayerBid()
case object Team extends PlayerBid
case object Alone extends PlayerBid


class Player(val team : Team, val decider : EuchreDecider)

case class Team(deciders : List[EuchreDecider]) {
  require(deciders.size == 2, "Else exactly two players per team")
  val players : List[Player] = for (d <- deciders) yield new Player(this, d) 
}

class EuchreGame(val rules : EuchreRules, teams_ : List[List[EuchreDecider]]) {
  val teams : List[Team] = for (t <- teams_) yield Team(t)
  def playGame(rules : EuchreRules) {
    def playRound(startState : EuchreRoundState, gameState : EuchreGameState) {
      val dealt = dealCards(startState.players, 5, shuffle(EuchreDeck, randy))
      val dealtState = EuchreDealtState(startState, dealt)
      def doBids(hands : List[(Player, List[Card])]) = {
        def doBids1(hands : List[(Player, List[Card])]) : Option[(Player, PlayerBid)] = {
          hands match {
            case Nil => None
            case (player,hand)::hs => 
              player.decider.pickItUp(dealtState, gameState) match {
                case None =>
                  doBids1(hands.tail)
                case Some(order) =>
                  Some((player, order))
              } 
          }
        }
        
        def doBids2(hands : List[(Player, List[Card])]) : (Player, Suit, PlayerBid) = {
          hands match {
            case (p,h)::Nil => 
              val (s,b) = p.decider.nameItForced(dealtState, gameState)
              (p,s,b)
            case (p,h)::hs => 
              p.decider.nameIt(dealtState, gameState) match {
                case Some((suit, bid)) => (p,suit,bid)
                case _ => doBids2(hands.tail)
              }
            case Nil => error("Unreachable")
          }
        }

        val (maker, suit, bid, dealerCard) = doBids1(hands) match {
          case Some((p,b)) =>
          (p, dealtState.flippedCard.suit, b, dealtState.flippedCard)
          case _ => 
            val (p,s,b) = doBids2(hands)
            (p,s,b,dealtState.hiddenCard)
        }
        EuchreBiddedState(dealtState, maker, suit, bid, dealerCard)
      }
      val biddedState = doBids(dealt._1)
      
      5
    }
    
    //Order the players appropriately
    val ps = {
      val ps = teams.flatMap(_.players)
      List(ps(0),ps(2),ps(1),ps(3))
    }
    
    playRound(EuchreRoundState(rotate(ps,randy.nextInt(4))), EuchreGameState(Nil))
  }
}
