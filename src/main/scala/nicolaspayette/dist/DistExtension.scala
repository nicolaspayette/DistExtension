package nicolaspayette.dist

import scala.collection.immutable.TreeMap
import scala.math.log10

import org.nlogo.api._
import org.nlogo.api.Syntax._
import org.nlogo.plot.PlotManager
import org.nlogo.nvm.ExtensionContext

class DistExtension extends DefaultClassManager {
  def load(manager: PrimitiveManager): Unit =
    for {
      (name, prim) ← Seq(
        "frequencies" -> new Frequencies,
        "ccdf" -> new Ccdf,
        "plot-points" -> new PlotPoints)
    } manager.addPrimitive(name, prim)
}

trait DistPrim extends DefaultReporter {

  type Points = Iterator[(Double, Double)]

  override def getSyntax = reporterSyntax(
    Array(ListType, BooleanType, BooleanType),
    ListType)

  def getNumbers(arg: Argument): Iterator[Double] =
    arg.getList.collect {
      case n: java.lang.Number ⇒ n.doubleValue
      case obj ⇒ throw new ExtensionException(
        "Expected a number but got " +
          Dump.logoObject(obj, true, false) + " instead.")
    }

  def getPoints(values: Iterator[Double]): Iterator[(Double, Double)]

  def logoListOfPoints(points: Points): LogoList =
    LogoList.fromIterator(points.map(p ⇒ LogoList(Double.box(p._1), Double.box(p._2))))

  def logPoints(points: Points, logX: Boolean, logY: Boolean): Iterator[(Double, Double)] =
    for {
      (x, y) ← points
      if !logX || x > 0
      _x = if (logX) log10(x) else x
      if !logY || y > 0
      _y = if (logY) log10(y) else y
    } yield (_x, _y)

  def report(args: Array[Argument], context: Context): AnyRef = {
    val values = getNumbers(args(0))
    val logX = args(1).getBooleanValue
    val logY = args(2).getBooleanValue
    logoListOfPoints(logPoints(getPoints(values), logX, logY))
  }

}

class Frequencies extends DistPrim {

  override def getPoints(values: Iterator[Double]): Iterator[(Double, Double)] =
    if (values.isEmpty) Iterator.empty else {
      val freqs = values
        .map(_.toInt)
        .toSeq
        .groupBy(identity)
        .mapValues(_.size)
      (for {
        x ← 0 to freqs.keys.max
        y = freqs.getOrElse(x, 0)
      } yield (x.toDouble, y.toDouble)).iterator
    }

}

class Ccdf extends DistPrim {

  def getPoints(values: Iterator[Double]): Iterator[(Double, Double)] = {
    val data = values.toSeq
    val n = data.size.toDouble
    data
      .sorted(Ordering[Double].reverse)
      .zip(Stream.from(1))
      .foldLeft(TreeMap[Double, Double]()) {
        case (m, (x, i)) ⇒ m + (x -> i / n)
      }
      .toIterator
  }

}

class PlotPoints extends DefaultCommand {

  override def getSyntax = commandSyntax(Array(ListType))

  def getXY(point: AnyRef): (Double, Double) = {
    try point match {
      case ll: LogoList ⇒ ll.toVector match {
        case Vector(x: java.lang.Double, y: java.lang.Double) ⇒ (x, y)
      }
    } catch {
      case e: MatchError ⇒ throw new ExtensionException(
        "Expected a list of two numbers but got " +
          Dump.logoObject(point, true, false) + " instead."
      )
    }
  }

  override def perform(args: Array[Argument], context: Context): Unit = {

    val points = args(0).getList
    val plot = context.asInstanceOf[ExtensionContext]
      .workspace
      .plotManager.asInstanceOf[PlotManager]
      .currentPlot.getOrElse(
        throw new ExtensionException(I18N.errors.get("org.nlogo.plot.noPlotSelected"))
      )
    val pen = plot
      .currentPen.getOrElse(
        throw new ExtensionException("Plot '" + plot.name + "' has no pens!")
      )

    pen.plotListenerReset(true)
    pen.hardReset()
    for {
      point ← points
      (x, y) = getXY(point)
    } pen.plot(x, y)
    plot.makeDirty()

  }
}
