package blue

import breeze.numerics.sqrt

object StatFunc {


  /**
   * Gives the first peak that satisfies the inputs and allows for ignoring noise
   * @param xArray the independent data series
   * @param yArray the dependent data series
   * @param neighbors number of data points to evaluate after a potential peak
   * @param percentDifference percentage to filter out noise where noise is difference between peak and avg value of neighbors
   * @param minCasePercent floor to start evaluating values with respect to maximum value percentage
   * @return a first peak coordinate that satisfies conditions
   */
  def firstMajorPeak(xArray: Array[Double], yArray: Array[Double], neighbors: Int, percentDifference: Double, minCasePercent: Double): (Double, Double) ={
    var avgSum: Double = 0.0
    var sum: Double = 0.0
    var minDifference: Double = 0.0
    val minCase = minCasePercent*.01*yArray.max
    val start = yArray.indexWhere(_ > minCase)
    if (start != -1) {
      for(i <- start to (xArray.length - neighbors - 1)){
          sum = 0.0
          for(neighbor <- (1 to neighbors)){
            sum += yArray(i + neighbor)
          }
          avgSum = sum/neighbors
          minDifference = .01*percentDifference*yArray(i)
          if(yArray(i) - avgSum > minDifference){
            return (xArray(i),yArray(i))
          }
      }
    }
    (-1, yArray(0))
  }

  /**
   * The correlation between two series of data ~1 = positive correlation,
   * ~0 = no correlation, ~-1 = -negative correlation
   * @param xArray the independent data series
   * @param yArray the dependent data series
   * @return the correlation number as a double
   */
  def correlation(xArray: Array[Double], yArray: Array[Double]):Double={
    var r = 0.0
    var x = 0.0
    var y = 0.0
    var x_2 = 0.0
    var y_2 = 0.0
    var xy = 0.0
    val n = xArray.length
    for(i <- (0 to (xArray.length - 1))){
      x += xArray(i)
      y += yArray(i)
      x_2 += (xArray(i)*xArray(i))
      y_2 += (yArray(i)*yArray(i))
      xy += (xArray(i)*yArray(i))
    }
    r = (n*xy - (x*y))/(sqrt(n*x_2 - (x*x)) * sqrt(n*y_2 - (y*y)))
    r
  }
}
