package geotrellis.server

import com.azavea.maml.ast.{Literal, MamlKind}
import geotrellis.raster.{ProjectedRaster, MultibandTile}
import cats.effect._
import simulacrum._

@typeclass trait TmsReification[A] {
  @op("tmsReification") def tmsReification(self: A, buffer: Int)(implicit contextShift: ContextShift[IO]): (Int, Int, Int) => IO[ProjectedRaster[MultibandTile]]
}

