package geotrellis.server.ogc

import opengis._
import opengis.wms._
import scalaxb._

import java.net.URI

import scala.xml.NamespaceBinding

package object wms {
  val wmsScope: NamespaceBinding = scalaxb.toScope(
    Some("ogc") -> "http://www.opengis.net/ogc",
    Some("wms") -> "http://www.opengis.net/wms",
    Some("xlink") -> "http://www.w3.org/1999/xlink",
    Some("xs") -> "http://www.w3.org/2001/XMLSchema",
    Some("xsi") -> "http://www.w3.org/2001/XMLSchema-instance"
  )

  /**
    * Default scope generates an incorrect XML file (in the incorrect scope, prefixes all XML elements with `wms:` prefix.
    *
    * val defaultScope = scalaxb.toScope(Some("ogc") -> "http://www.opengis.net/ogc",
    * Some("wms") -> "http://www.opengis.net/wms",
    * Some("xlink") -> "http://www.w3.org/1999/xlink",
    * Some("xs") -> "http://www.w3.org/2001/XMLSchema",
    * Some("xsi") -> "http://www.w3.org/2001/XMLSchema-instance")
    */
  val constrainedWMSScope: NamespaceBinding = scalaxb.toScope(
    Some("ogc") -> "http://www.opengis.net/ogc",
    Some("xlink") -> "http://www.w3.org/1999/xlink",
    Some("xs") -> "http://www.w3.org/2001/XMLSchema",
    Some("xsi") -> "http://www.w3.org/2001/XMLSchema-instance"
  )

  implicit class withLegendModelMethods(that: LegendModel) {
    def toLegendURL: LegendURL =
      LegendURL(
        Format = that.format,
        OnlineResource = that.onlineResource.toOnlineResource,
        attributes = Map("@width" -> DataRecord(BigInt(that.width)), "@height" -> DataRecord(BigInt(that.height)))
      )
  }

  implicit class withOnlineResourceModelMethods(that: OnlineResourceModel) {
    def toOnlineResource: OnlineResource =
      OnlineResource(Map(
        "@{http://www.w3.org/1999/xlink}type" -> Option(DataRecord(xlink.TypeType.fromString(that.`type`, scope = toScope(Some("xlink") -> "http://www.w3.org/1999/xlink")))),
        "@{http://www.w3.org/1999/xlink}href" -> Option(DataRecord(new URI(that.href))),
        "@{http://www.w3.org/1999/xlink}role" -> that.role.map(v => DataRecord(new URI(v))),
        "@{http://www.w3.org/1999/xlink}title" -> that.title.map(v => DataRecord(v)),
        "@{http://www.w3.org/1999/xlink}show" -> that.show.map(v => DataRecord(xlink.ShowType.fromString(v, scope = scalaxb.toScope(Some("xlink") -> "http://www.w3.org/1999/xlink")))),
        "@{http://www.w3.org/1999/xlink}actuate" -> that.actuate.map(v => DataRecord(xlink.ActuateType.fromString(v, scope = scalaxb.toScope(Some("xlink") -> "http://www.w3.org/1999/xlink"))))
      ).collect { case (k, Some(v)) => k -> v })
  }
}
