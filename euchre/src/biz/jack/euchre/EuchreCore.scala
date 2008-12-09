package biz.jack.euchre

/*
import cards.Cards._
import util.BenPredef._

object EuchreCore {
  val WinningScore = 10;
  val randy = new java.util.Random
}

import EuchreCore._


abstract case class OrderItUpResult() 
case object OrderItUpPass extends OrderItUpResult
case object OrderItUp extends OrderItUpResult
case object OrderItUpAlone extends OrderItUpResult

trait Player {
  //Return (false,?)=no, (true,false)=yes, team (true, true) = yes, going alone
  def ordersItUp(card : Card, hand : List[Card]) : OrderItUpResult
  def nameIt(badSuit : Suit, hand : List[Card]) : Option[(Suit,Boolean)]
  def forcedToNameIt(badSuit : Suit, hand : List[Card]) : (Suit,Boolean)
  def dropCard(trump : Suit, newCard : Card) : Card
  def playCard(cardsInPlay : List[Card], hand : List[Card], judge : EuchreJudge) : Card
}
case class Team(val players : List[Player]) {
  require(players.size == 2, "Only two players per team allowed")
}
case class TeamPlayer(team : Team, player : Player)
case class TeamPlayerCard(team : Team, player : Player, card : Card)
case class TeamPlayerHand(team : Team, player : Player, hand : List[Card])

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

case class Contract(trump : Suit, maker : TeamPlayer, alone : Boolean)

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
        //Deal the cards to the players
        val (hands, deck) = dealCards(players)
        
        //Get the contract
        val contract = playCallRound(hands, deck.head)
        
        //Let the dealer replace his card
        val hands2 = {
          val replacementCard = if (contract.trump == deck.head.suit) {
            deck.head
          } else {
            deck.tail.head
          }
          val dealer = hands.last
          val removedCard = dealer.player.dropCard(contract.trump, replacementCard)
          val newDealerHand = replacementCard::dealer.hand.filter(removedCard!=)
          hands.dropRight(1):::TeamPlayerHand(dealer.team, dealer.player, newDealerHand)::Nil
        }
        
        if (contract.alone) {
        //filter out for going alone
          (hands2.filter(x => contract.maker.team != x.team || contract.maker.player == x.player),contract)
        } else {
          (hands2,contract)
        }
      }
      val (hands,contract) = preMainRound
      
      val tricks = playMainRound(hands, new EuchreJudge(contract.trump))
      
      def totalTricks(tricks : List[Team], map : Map[Team, Int]) : (Team, Int)= {
        if (tricks == Nil) {
          //Get the team that won the most tricks and how many they won
          def maxer(high : (Team, Int), current : (Team, Int)) = {
            if (current._2 > high._2) current else high
          } 
          map.toList.foldLeft((null : Team, 0))(maxer _)
        } else {
          //Put all of the tricks into a map
          val t = tricks.head
          val newMap = map + ((t, map.get(t).getOrElse(0) + 1))
          totalTricks(tricks.tail, newMap)
        }
      }
      
      val (winningTeam,score) = totalTricks(tricks, Map());
      val gp = g + RoundScore(winningTeam, scoreRound(contract, winningTeam, score))
      if (gp.isOver) {
        gp
      } else {
        playRound(rotatePlayers(players,1), gp)
      }
    }
    
    val p1::p2::p3::p4::Nil = for (t <- teams; p <- t.players) yield TeamPlayer(t,p)
    playRound(rotatePlayers(p1::p3::p2::p4::Nil, randy.nextInt(4)), GameScore(Map() ++ (teams.map((_,0)))))
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
    
    def orderItUp(players : List[TeamPlayerHand]) : Option[(TeamPlayer,Boolean)] = players match {
      case p::ps =>
        p.player.ordersItUp(topCard, p.hand) match {
          case OrderItUpPass =>
            orderItUp(ps)  
          case OrderItUp =>
            Some((TeamPlayer(p.team,p.player),false))
          case OrderItUpAlone =>
            Some((TeamPlayer(p.team,p.player),true))
        }
      case Nil => None
    }
    
    def nameIt(players : List[TeamPlayerHand]) : Contract = players match {
      case p::Nil =>
        val (suit,alone) = p.player.forcedToNameIt(topCard.suit, p.hand)
        Contract(suit, TeamPlayer(p.team,p.player), alone)
      case p::ps => 
        val s = p.player.nameIt(topCard.suit, p.hand)
        if (s.isDefined) {
          Contract(s.get._1, TeamPlayer(p.team,p.player), s.get._2)
        } else {
          nameIt(ps)
        }
      case Nil => error("Unexepected")
    }
    
    orderItUp(players) match {
      case Some((tp, alone)) => Contract(topCard.suit, tp, alone)
      case None => nameIt(players)
    }
  }
  
  def playMainRound(hands : List[TeamPlayerHand], judge : EuchreJudge) : List[Team] = {
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
        def playCards(cardsInPlay : List[TeamPlayerCard], hands : List[TeamPlayerHand]) : List[TeamPlayerCard] = {
          hands match {
            case Nil =>
              cardsInPlay
            case h::hs =>
              playCards(cardsInPlay:::TeamPlayerCard(h.team, h.player, h.player.playCard(cardsInPlay.map(_.card),h.hand,judge))::Nil,hs)
          }
        }
        
        val tpcs = playCards(Nil,hands)
        val winningCard = judge.getWinner(tpcs.map(_.card))
        val winningTPC = tpcs.find(_.card==winningCard).get
        winningTPC.team::playTrick(rotateToPlayer(hands, winningTPC.player))
      }
    }
    playTrick(hands)
  }
  
  def scoreRound(contract : Contract, winner : Team, tricks : Int) : Int = {
    val swept = tricks == 5
    val defenderWins = contract.maker.team != winner 
    if (defenderWins) {
      //Defender wins
      2
    } else {
      //Makers win
      if (contract.alone) {
        if (swept) 4 else 1
      } else {
        if (swept) 2 else 1
      }
    }
  }
}

*/