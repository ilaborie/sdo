package model.contact

/**
 * EMail
 * @param email the email
 */
case class EMail(email:String) {
  require(email.contains('@'))

  val name = email.split('@')(0)
  val server = email.split('@')(1)
}
