package geotrellis.server.ogc

import geotrellis.raster._
import geotrellis.raster.histogram.Histogram
import geotrellis.raster.render.{ColorMap, ColorRamp}

trait OgcStyle {
  def name: String
  def title: String
  def legends: List[LegendModel]
  def renderImage(mbtile: MultibandTile, format: OutputFormat, hists: List[Histogram[Double]]): Array[Byte]
}

case class ColorMapStyle(
  name: String,
  title: String,
  colorMap: ColorMap,
  legends: List[LegendModel] = Nil
) extends OgcStyle {
  def renderImage(
    mbtile: MultibandTile,
    format: OutputFormat,
    hists: List[Histogram[Double]]
  ): Array[Byte] = {
    format match {
      case format: OutputFormat.Png =>
        format.render(mbtile.band(bandIndex = 0), colorMap)

      case OutputFormat.Jpg =>
        mbtile.band(bandIndex = 0).renderJpg(colorMap).bytes

      case OutputFormat.GeoTiff => ??? // Implementation necessary
    }
  }
}

case class ColorRampStyle(
  name: String,
  title: String,
  colorRamp: ColorRamp,
  stops: Option[Int],
  legends: List[LegendModel] = Nil
) extends OgcStyle {
  def renderImage(
    mbtile: MultibandTile,
    format: OutputFormat,
    hists: List[Histogram[Double]]
  ): Array[Byte] = {
    val numStops: Int = stops.getOrElse(colorRamp.colors.length)
    val ramp: ColorRamp = colorRamp.stops(numStops)

    // we're assuming the layers are single band rasters
    val cmap = ColorMap.fromQuantileBreaks(hists.head, ramp)
    format match {
      case format: OutputFormat.Png =>
       format.render(mbtile.band(bandIndex = 0), cmap)

      case OutputFormat.Jpg =>
        mbtile.band(bandIndex = 0).renderJpg(cmap).bytes

      case OutputFormat.GeoTiff => ??? // Implementation necessary
    }
  }
}

case class LegendModel(
  format: String,
  width: Int,
  height: Int,
  onlineResource: OnlineResourceModel
)

case class OnlineResourceModel(
  `type`: String,
  href: String,
  role: Option[String] = None,
  title: Option[String] = None,
  show: Option[String] = None,
  actuate: Option[String] = None
)
