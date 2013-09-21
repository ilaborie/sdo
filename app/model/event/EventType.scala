// The MIT License (MIT)
//
// Copyright (c) 2013 Igor Laborie
//
// Permission is hereby granted, free of charge, to any person obtaining a copy of
// this software and associated documentation files (the "Software"), to deal in
// the Software without restriction, including without limitation the rights to
// use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
// the Software, and to permit persons to whom the Software is furnished to do so,
// subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in all
// copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
// FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
// COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
// IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
// CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

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
