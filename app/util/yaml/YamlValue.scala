package util.yaml


/**
 * Generic json value
 */
sealed trait YamlValue {

  /**
   * Return the property corresponding to the fieldName, supposing we have a YAML Object.
   *
   * @param fieldName the name of the property to lookup
   * @return the resulting YamlValue. If the current node is not a YAML Object or doesn't have the property, a YamlUndefined will be returned.
   */
  def \(fieldName: String): Option[YamlValue] = None

  /**
   * Lookup for fieldName in the current object and all descendants.
   *
   * @return the list of matching nodes
   */
  def \\(fieldName: String): Seq[YamlValue] = Nil

  override def toString = ???

}

/**
 * Represent a  Yaml string value.
 */
case class YamlString(value: String) extends YamlValue

/**
 * Represent a  Yaml number value.
 */
case class YamlNumber(value: BigDecimal) extends YamlValue

/**
 * Represent a  Yaml array value.
 */
case class YamlArray(value: Seq[YamlValue] = List()) extends YamlValue {

  /**
   * Access a value of this array.
   *
   * @param index Element index.
   */
  def apply(index: Int): YamlValue = value(index)

  /**
   * Lookup for fieldName in the current object and all descendants.
   *
   * @return the list of matching nodes
   */
  override def \\(fieldName: String): Seq[YamlValue] = value.flatMap(_ \\ fieldName)

  /**
   * Concatenates this array with the elements of an other array.
   */
  def ++(other: YamlArray): YamlArray =
    YamlArray(value ++ other.value)

  /**
   * Append an element to this array.
   */
  def :+(el: YamlValue): YamlArray = YamlArray(value :+ el)

  def append(el: YamlValue): YamlArray = this.:+(el)

  /**
   * Prepend an element to this array.
   */
  def +:(el: YamlValue): YamlArray = YamlArray(el +: value)

  def prepend(el: YamlValue): YamlArray = this.+:(el)

}

/**
 * Represent a  Yaml object value.
 */
case class YamlObject(fields: Seq[(String, YamlValue)]) extends YamlValue {

  lazy val value: Map[String, YamlValue] = fields.toMap

  /**
   * Return the property corresponding to the fieldName, supposing we have a YAML Object.
   *
   * @param fieldName the name of the property to lookup
   * @return the resulting YamlValue. If the current node is not a YAML Object or doesn't have the property, a YamlUndefined will be returned.
   */
  override def \(fieldName: String): Option[YamlValue] = {
    val v = value.get(fieldName)
    if (v.isDefined) v
    else super.\(fieldName)
  }

  /**
   * Lookup for fieldName in the current object and all descendants.
   *
   * @return the list of matching nodes
   */
  override def \\(fieldName: String): Seq[YamlValue] = {
    value.foldLeft(Seq[YamlValue]())((o, pair) => pair match {
      case (key, v) if key == fieldName => o ++ (v +: (v \\ fieldName))
      case (_, v) => o ++ (v \\ fieldName)
    })
  }

  /**
   * Return all keys
   */
  def keys: Set[String] = fields.map(_._1).toSet

  /**
   * Return all values
   */
  def values: Set[YamlValue] = fields.map(_._2).toSet

  def fieldSet: Set[(String, YamlValue)] = fields.toSet

  /**
   * Merge this object with an other one. Values from other override value of the current object.
   */
  def ++(other: YamlObject): YamlObject =
    YamlObject(fields.filterNot(field => other.keys(field._1)) ++ other.fields)

  /**
   * removes one field from YamlObject
   */
  def -(otherField: String): YamlObject =
    YamlObject(fields.filterNot(_._1 == otherField))

  /**
   * adds one field from YamlObject
   */
  def +(otherField: (String, YamlValue)): YamlObject =
    YamlObject(fields :+ otherField)

  override def equals(other: Any): Boolean =
    other match {
      case that: YamlObject =>
        (that canEqual this) &&
          fieldSet == that.fieldSet
      case _ => false
    }

  def canEqual(other: Any): Boolean = other.isInstanceOf[YamlObject]

  override def hashCode: Int = fieldSet.hashCode()

}
