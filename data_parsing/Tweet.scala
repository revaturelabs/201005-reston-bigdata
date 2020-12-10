package data_parsing

case class Tweet(
                  timestamp: String = null,
                  id: Long = 0,
                  text: String = null,
                  // truncated: Boolean,
                  // coordinates: Option[GeoJSON],
                  // place: Option[TwitterPlace]
                ) {
  def getHashtags: List[String] = {
    val re = """(#\S+)""".r
    re.findAllIn( text ).toList
  }
}
