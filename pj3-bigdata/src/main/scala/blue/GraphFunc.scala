package blue

import breeze.plot.{Figure, plot}
import org.jfree.chart.title.Title
import org.jfree.chart.annotations.XYTextAnnotation

object GraphFunc {


  def graph(xData: Array[Double], yData: Array[Double], style: Char = '.', name: String = null,legend: Boolean = false, title: String = "X vs Y Plot", xlabel: String = "X", ylabel: String = "Y"): Unit={
    val fig = Figure()
    val p = fig.subplot(0)
    p += plot(xData,yData, style=style, name = name)
    p.legend = legend
    p.title = title
    p.ylabel = ylabel
    p.xlabel = xlabel
    fig.refresh()
  }

  def hist(data: Array[Double], bins: Int,  name: String = null,legend: Boolean = false, title: String = "Data Histogram", xlabel: String = "Data", ylabel: String = "Bin Size"): Unit={
    val fig = Figure()
    val p = fig.subplot(0)
    p += breeze.plot.hist(data, bins, name = name)
    p.legend = legend
    p.title = title
    p.ylabel = ylabel
    p.xlabel = xlabel
    fig.refresh()
  }

  def graphSeries(xData: Array[Array[Double]], yData: Array[Array[Double]], name: Array[String] = null,style: Char = '.', legend: Boolean = false, title: String = "X vs Y Plot", xlabel: String = "X", ylabel: String = "Y"): Unit={
    val fig = Figure()
    val p = fig.subplot(0)
    for(series <- 0 to xData.length-1){
      p += plot(xData(series),yData(series), style=style, name = name(series))
    }
    p.legend = legend
    p.title = title
    p.ylabel = ylabel
    p.xlabel = xlabel
    fig.refresh()
  }
}
