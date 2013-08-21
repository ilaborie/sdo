package model.event

/**
  */
sealed abstract class EventType {
  def style: String
  def styles: String = style
}

object EventType {

  val all: List[EventType] = List(WorldEvent, FranceEvent, LigueEvent, ComiteEvent, TeamEvent)

  def apply(evtType: String): EventType = {
    evtType match {
      case "world france" => WorldFranceEvent
      case "world" => WorldEvent
      case "france" => FranceEvent
      case "fede" => FederationEvent
      case "ligue" => LigueEvent
      case "comite" => ComiteEvent
      case _ => DefaultEvent
    }
  }
}


object WorldEvent extends EventType {
  val style = "world"
}

object WorldFranceEvent extends EventType {
  val style = "worldfrance"
  override val styles= "world france"
}

object FranceEvent extends EventType {
  val style = "france"
}

object FederationEvent extends EventType {
  val style = "fede"
}

object LigueEvent extends EventType {
  val style = "ligue"
}

object ComiteEvent extends EventType {
  val style = "comite"
}

object TeamEvent extends EventType {
  val style = "team"
}


object DefaultEvent extends EventType {
  val style = "default"
}
