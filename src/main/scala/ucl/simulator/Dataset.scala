package ucl.simulator

import java.nio.file.{Files, Path}

import com.google.common.collect.ImmutableMap
import io.lumos.core.formats.DataReader
import io.lumos.core.formats.csv.CsvDataFormat
import io.lumos.core.types.{Datum, Row, Schema}

import scala.collection.JavaConverters._
import scala.reflect.ClassTag
import scala.util.control.NonFatal

final class Dataset[T <: Product : ClassTag](schema: Schema, rootDir: Path) {
  private val ctor = implicitly[ClassTag[T]].runtimeClass.getConstructors.head

  def write(key: String, items: TraversableOnce[T]): Unit = {
    val writer = Dataset.format.newWriter(pathFor(key), schema, ImmutableMap.of())
    try {
      items.foreach(item => writer.write(toRow(item)))
    } finally {
      writer.close()
    }
  }

  def read(key: String): Iterator[T] = readFile(pathFor(key))

  def read(): Iterator[T] = listFiles.map(readFile).foldLeft(Iterator.empty.asInstanceOf[Iterator[T]])(_ ++ _)

  private class RecordIterator(reader: DataReader) extends Iterator[T] {
    private[this] var nextRecord: Option[T] = None
    private[this] var closed = false

    override def hasNext: Boolean = {
      maybeReadNext()
      nextRecord.isDefined
    }

    override def next(): T = {
      maybeReadNext()
      val result = nextRecord.get
      nextRecord = None
      result
    }

    private def maybeReadNext(): Unit = {
      if (!closed && nextRecord.isEmpty) {
        try {
          Option(reader.readNext()) match {
            case Some(row) => nextRecord = Some(toPojo(row))
            case None =>
              closed = true
              reader.close()
          }
        } catch {
          case NonFatal(e) =>
            closed = true
            reader.close()
            throw e
        }
      }
    }
  }

  private def readFile(file: Path): Iterator[T] = {
    if (!Files.exists(file)) {
      Iterator.empty
    } else {
      val reader = Dataset.format.newReader(file, schema, ImmutableMap.of())
      new RecordIterator(reader)
    }
  }

  private def listFiles: Iterator[Path] = {
    val files = Files.list(rootDir).iterator.asScala
    files.filter(f => extractExtension(f.getFileName.toString) == "csv")
  }

  private def extractExtension(filename: String): String = {
    val pos = filename.lastIndexOf(".")
    if (pos > -1) filename.drop(pos + 1) else ""
  }

  private def toRow(obj: T): Row = new Row(obj.productIterator.toArray.map(v => Datum.of(v.asInstanceOf[AnyRef])): _*)

  private def toPojo(row: Row): T = {
    ctor.newInstance(row.toArray: _*).asInstanceOf[T]
  }

  private def pathFor(key: String) = rootDir.resolve(s"$key.csv")
}

object Dataset {
  private val format = new CsvDataFormat
}