package biz.jack.euchre


import cards.Cards._

object EuchreCore {
  val WinningScore = 10;
  val randy = new java.util.Random
}

import EuchreCore._

trait Player {
  def ordersItUp : Boolean
  def namesIt : Boolean
}

class EuchreScoreKeeper {
  
}

class Team(val players : List[Player]) {
  require(players.size == 2, "Only two players per team allowed")
}

case class TeamPlayer(team : Team, player : Player) {
  
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
      
      val contract = playCallRound(players)
      
      val gp = g + scoreRound()
      if (gp.isOver) {
        gp
      } else {
        playRound(rotatePlayers(players,1), gp)
      }
    }
    
    val p1::p2::p3::p4::Nil = for (t <- teams; p <- t.players) yield TeamPlayer(t,p)
    playRound(rotatePlayers(p1::p3::p2::p4::Nil, randy.nextInt(4)), 
              GameScore(Map() ++ (for (t <- teams) yield (t,0))))
  }
  
  def dealCards = {
    
  }
  
  def playCallRound(players : List[TeamPlayer]) : Contract = {
    val tp = for {
      p <- players
    }
  }
  
  def playMainRound {
    
  }
  
  def scoreRound {
    
  }
  
}

