import org.apache.spark.SparkContext
import org.apache.spark.SparkConf
import org.apache.spark.mllib.linalg.Matrices
import org.apache.spark.mllib.linalg.distributed.BlockMatrix

object Main {
    def main(args: Array[String]) {
        val conf = new SparkConf().setMaster("local").setAppName("My App")
        val sc = new SparkContext(conf)

        val rowParallelism = 4
        val colParallelism = 4
        val rowBlockSize = 2048
        val colBlockSize = 2048

        val rows = rowParallelism * rowBlockSize
        val cols = colParallelism * colBlockSize

        val rowBlocks = rows/rowBlockSize
        val colBlocks = cols/colBlockSize

        val left_rdd = sc.parallelize( {for(i <- 0 until rowBlocks; j <- 0 until colBlocks) yield (i, j)}, rowParallelism*colParallelism).map( coord => (coord, Matrices.rand(rowBlockSize, colBlockSize, util.Random.self)))

        val right_rdd = sc.parallelize( {for(i <- 0 until rowBlocks; j <- 0 until colBlocks) yield (i, j)}, rowParallelism*colParallelism).map( coord => (coord, Matrices.rand(rowBlockSize, colBlockSize, util.Random.self)))

        val left_bm = new BlockMatrix(left_rdd, rowBlockSize, colBlockSize).cache()
        val right_bm = new BlockMatrix(right_rdd, rowBlockSize, colBlockSize).cache()

        val t = System.nanoTime()
        val result = left_bm.multiply(right_b,4)
        result.validate()
        val latency = (System.nanoTime() - t) / 1e9
        println(latency)
        println("finish!!!")
    }
}
