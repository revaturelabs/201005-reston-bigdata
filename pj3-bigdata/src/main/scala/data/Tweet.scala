package data_parsing

/**
 * Object that stores relevant information that is provided by Twitter API.
 *
 * @param timestamp: String noting time of tweet creation
 * @param id: Unique identifier for tweet. Lower numbers are older tweets.
 * @param text: Contents of tweet. Up to 280 character if Extended Mode is
 *  used in all API calls that obtain tweets. If Extended Mode is not planned to
 *  be used in all cases, please modify this class to be able ot take advantage
 *  of the "truncated" boolean so that it will be tracked when a tweet is not
 *  showing its full text.
 * @param countryCode: Code obtained from the internal TwitterPlace object,
 *  which has only one field which is relevant to our code. It is the
 *  responsibility of the Parser to obtain country code from the provided
 *  TwitterPlace.
 */
case class Tweet(
                  timestamp: String = null,
                  id: Long = 0,
                  text: String = null,
                  countryCode: String = null
                ) {
  def getHashtags: List[String] = {
    val re = """(#\S+)""".r
    re.findAllIn( text ).toList
  }
}
