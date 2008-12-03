package biz.jack.euchre

import cards.Cards._
import util.BenPredef._

//Determine the rank of cards
class EuchreJudge(val trump : Suit) {
  //Returns the winning card, 
  //the first card in the list must be the card that was lead
  def isHighBower(c : Card) = c.suit == trump && c.rank == RankJ
  def isLowBower(c : Card) = c.suit == otherSuitSameColor(trump) && c.rank == RankJ
  def getEffectiveSuit(c : Card) = isLowBower(c).ifElse(trump, c.suit)
  def isTrump(c : Card) = getEffectiveSuit(c) == trump
  def getWinner[A <: Card](cards : List[A]) : Card = {
    require(cards != Nil, "Cannot judge 0 cards")
    val cardLed = cards.head
    def isSuitLed(c : Card) = getEffectiveSuit(c) == getEffectiveSuit(cardLed)
    // if a outranks b 1, if they are equal 0 else -1
    def compare(a : Card, b : Card) : Int = {
      (a,b) match {
        case (a,_) if (isHighBower(a)) => 1
        case (_,b) if (isHighBower(b)) => -1
        case (a,_) if (isLowBower(a)) => 1
        case (_,b) if (isLowBower(b)) => -1
        case (a,b) if (isTrump(a) && isTrump(b)) => a.rank.compare(b.rank)
        case (a,_) if (isTrump(a)) => 1
        case (_,b) if (isTrump(b)) => -1
        case (a,b) if (isSuitLed(a) && isSuitLed(b)) => a.rank.compare(b.rank)
        case (a,_) if (isSuitLed(a)) => 1
        case (_,b) if (isSuitLed(b)) => -1
        case _ => 0
      }
    }
    def recur(cards : List[A], highCard : Card) : Card = {
     cards match {
       case c::cs =>
         recur(cs, if (compare(c,highCard)>0) c else highCard)
       case Nil => highCard
     }
    }
    recur(cards, Card(Rank2, Clubs))
  }
  
  //Returns true if a card can be thrown, given that a certain card was lead
  def isLegal(cardLed : Card, cardThrown : Card, hand : List[Card]) : Boolean = {
    val suitLed = getEffectiveSuit(cardLed)
    if (suitLed == getEffectiveSuit(cardThrown)) {
      true
    } else {
      //Ensure that the hand doesn't have any cards of the suit
      hand.filter(_ != cardThrown).map(getEffectiveSuit).filter(suitLed==).isEmpty
    }
  }
}
