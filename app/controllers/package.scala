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

import model.orga._
import play.api.mvc._

/**
 * User: igorlaborie
 * Date: 28/07/13
 * Time: 09:34
 */
package object controllers {

  /**
   * A Ligue action
   * @param ligueShortName the ligue short name
   * @param fun a function
   * @return the results
   */
  case class LigueAction(ligueShortName: String)(val fun: Ligue => Result) {
    val result = Ligue.findByShortName(ligueShortName) match {
      case Some(ligue) => fun(ligue)
      case _ => Results.BadRequest(s"Ligue non connue: $ligueShortName")
    }
  }


  /**
   * A comité action
   * @param ligueShortName the ligue short name
   * @param comiteShortName the comite short name
   * @param fun a function
   * @return the results
   */
  case class ComiteAction(ligueShortName: String, comiteShortName: String)(val fun: Comite => Result) {
    val result = Ligue.findByShortName(ligueShortName) match {
      case Some(ligue) => ligue.comites.find(_.shortName == comiteShortName) match {
        case Some(comite) => fun(comite)
        case _ => Results.BadRequest(s"Comite non connue: $comiteShortName dans la ligue $ligue")
      }
      case _ => Results.BadRequest(s"Ligue non connue: $ligueShortName")
    }
  }

}
