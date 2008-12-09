package biz.jack.euchre

import util.BenPredef._
import cards.Cards._

case class EuchreRules()

object EuchreGame {
  val randy = new java.util.Random
}

import EuchreGame._



object EuchreGameStates {
  case class GameState(val rounds : List[RoundState])
  case class RoundState(val players : List[Player]) {
    lazy val dealer = players.last
  }
  case class DealtState(val pre : RoundState, val dealt : (List[(Player, List[Card])],List[Card])) {
    lazy val flippedCard = dealt._2.head
    lazy val hiddenCard = dealt._2.tail.head
  }
  case class BiddedState(val pre : DealtState, val maker : Player, val suit : Suit, val bid : TeamOrAlone, val dealerCard : Card) {
    lazy val wasOrderedUp = pre.flippedCard == dealerCard
  }
  case class DiscardedState(val pre : BiddedState, val hands : List[(Player, List[Card])])
}

import EuchreGameStates._

trait EuchreDecider {
  def pickItUp(dealtState : DealtState, gameState : GameState) : Option[TeamOrAlone]
  def nameIt(dealtState : DealtState, gameState : GameState) : Option[(Suit, TeamOrAlone)]
  def nameItForced(dealtState : DealtState, gameState : GameState) : (Suit, TeamOrAlone)
  def discard(biddedState : BiddedState, gameState: GameState) : (Card)
}

case class TeamOrAlone()
case object Team extends TeamOrAlone
case object Alone extends TeamOrAlone


class Player(val team : Team, val decider : EuchreDecider)

case class Team(deciders : List[EuchreDecider]) {
  require(deciders.size == 2, "Else exactly two players per team")
  val players : List[Player] = for (d <- deciders) yield new Player(this, d) 
}

class EuchreGame(val rules : EuchreRules, teams_ : List[List[EuchreDecider]]) {
  val teams : List[Team] = for (t <- teams_) yield Team(t)
  def playGame(rules : EuchreRules) {
    def playRound(startState : RoundState, gameState : GameState) {
      val dealt = dealCards(startState.players, 5, shuffle(EuchreDeck, randy))
      val dealtState = DealtState(startState, dealt)
      def doBids(hands : List[(Player, List[Card])]) = {
        def doBids1(hands : List[(Player, List[Card])]) : Option[(Player, TeamOrAlone)] = {
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
        
        def doBids2(hands : List[(Player, List[Card])]) : (Player, Suit, TeamOrAlone) = {
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
        BiddedState(dealtState, maker, suit, bid, dealerCard)
      }
      val biddedState = doBids(dealt._1)
      //Get the dealer discard
      val discard = startState.dealer.decider.discard(biddedState, gameState)
      //Create the dealers new hand
      val dh = dealtState.dealt._1.flatMap(p=>if (p._1 == startState.dealer) p._2 else Nil).map(c=> if (c == discard) biddedState.dealerCard else c)
      val newHands = dealtState.dealt._1.map(p=>if (p._1 == startState.dealer) (p._1,dh) else p)
      //Todo Ensure that the dealer's hand has that card in it
      val discardedState = DiscardedState(biddedState, newHands)
      
      5
    }
    
    //Order the players appropriately
    val ps = {
      val ps = teams.flatMap(_.players)
      List(ps(0),ps(2),ps(1),ps(3))
    }
    
    playRound(RoundState(rotate(ps,randy.nextInt(4))), GameState(Nil))
  }
}
