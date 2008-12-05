package biz.jack.euchre

import cards.Cards._


//Simple Euchre ai player
class ComputerEuchrePlayer extends Player {
  def ordersItUp(card : Card, hand : List[Card]) : OrderItUpResult = {
    val judge = new EuchreJudge(card.suit)
    val count = hand.foldLeft(0.0){(a,c) =>
      if (judge.isHighBower(c)) {
        1.0
      } else if (judge.isLowBower(c)) {
        .9
      } else if (c.rank == RankA) {
        .7
      } else if (judge.isTrump(c)){
        .5
      } else {
        0.0
      } + a
    }
    
    if (count >= 3.0) {
      OrderItUpAlone
    } else if (count >= 2.3) {
      OrderItUp
    } else {
      OrderItUpPass
    }
  }
  
  def nameIt(badSuit : Suit, hand : List[Card]) : Option[(Suit,Boolean)] = {
    
  }
  
  def forcedToNameIt(badSuit : Suit, hand : List[Card]) : (Suit,Boolean) = {
    
  }
  
  def dropCard(newCard : Card) : Card = {
    
  }
  
  def playCard(cardsInPlay : List[Card], hand : List[Card], judge : EuchreJudge) : Card = {
    
  }
  
}
