package biz.jack.euchre

import cards.Cards._

//Determine the rank of cards
class EuchreJudge(val trump : Suit) {
  //Returns the winning card
  def apply[A <: Card](cards : List[A]) : Card = {
    require(cards != Nil, "Cannot judge 0 cards")
    val suitLed = cards.head
    def highestOf(a : Card, b : Card) : Card = {
    }
    def recur(cards : List[A], highCard : Card) : Card = {
     cards match {
       case c::cs =>
         recur(cs, highestOf(c,highCard))
       case Nil => highCard
     }
    }
    recur(cards, Card(Rank2, Clubs))
  }
}
