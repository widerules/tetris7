package biz.jack.cards



object Cards {
  
  abstract case class SuitColor()
  case object SuitRed extends SuitColor
  case object SuitBlack extends SuitColor
  
  abstract case class Suit(name : String, color : SuitColor)

  case object Clubs extends Suit("c", SuitBlack)
  case object Diamonds extends Suit("d", SuitRed)
  case object Hearts extends Suit("h", SuitRed)
  case object Spades extends Suit("s", SuitBlack)
  
  val Suits = List(Clubs, Diamonds, Hearts, Spades)
  
  case class Rank(name : String, strength : Int) extends Ordered[Rank] {
    override def compare(other : Rank) = strength.compare(other.strength)
  }
  
  case object Rank2 extends Rank("2", 0)
  case object Rank3 extends Rank("3", 1)
  case object Rank4 extends Rank("4", 2)
  case object Rank5 extends Rank("5", 3)
  case object Rank6 extends Rank("6", 4)
  case object Rank7 extends Rank("7", 5)
  case object Rank8 extends Rank("8", 6)
  case object Rank9 extends Rank("9", 7)
  case object Rank10 extends Rank("10", 8)
  case object RankJ extends Rank("J", 9)
  case object RankQ extends Rank("Q", 10)
  case object RankK extends Rank("K", 11)
  case object RankA extends Rank("A", 12)
  
  val Ranks = List(Rank2, Rank3, Rank4, Rank5, Rank6, Rank7, Rank8, Rank9, Rank10, RankJ, RankQ, RankK, RankA)
  
  val FullDeck : List[Card] = for (s <- Suits; r <- Ranks) yield Card(r,s)
  val EuchreDeck : List[Card] = for (c <- FullDeck if (c.rank >= Rank9 && c.rank <= RankA)) yield c 
 
  case class Card(rank : Rank, suit : Suit) {}
}
