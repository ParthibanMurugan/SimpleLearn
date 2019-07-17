package com.simplilearn.bigdata.casestudy_10

import org.apache.spark.sql._
import org.apache.spark.sql.types._

object Solution_3 {

  def main(args: Array[String]): Unit = {

    if (args.length != 2) {
      System.out.println("Please provide <input_path> <spark_master>")
      System.exit(0)
    }
    val inputPath: String = args(0)

    val dataset = readFile(inputPath, readWithHeader(getSparkSession("companies-analysis", args(1))))

    getData(dataset)
  }

  def getData(dataset: Dataset[Row]): Unit = {
    val modifiedDataset = dataset
        .filter("country != ''")
        .select("industry")
        .groupBy("industry")
        .count()
        .sort(functions.desc("count"))
    System.out.println("Top five Industries having maximum companies")
    modifiedDataset.take(5).foreach(
      row => {
        System.out.println("Industry = "+row.get(0).toString+" Companies Count = "+row.get(1))
      }
    )
  }

  def getSparkSession(appName: String, master: String) = {
    val sparkSession = SparkSession.builder.appName(appName).master(if (master.equalsIgnoreCase("local")) "local[*]"
    else master).getOrCreate
    System.out.println("Spark version " + sparkSession.version)
    sparkSession
  }

  def readFile(path: String, dataFrameReader: DataFrameReader) = {
    System.out.println("Reading file " + path)
    val dataset = dataFrameReader.csv(path)
    System.out.println("Dataset Schema " + dataset.schema)
    System.out.println("Row Count" + dataset.count())
    dataset
  }

  def readWithHeader(sparkSession: SparkSession) = {
    val transactionSchema = StructType(Array(
      StructField("name", StringType, true),
      StructField("domain", StringType, true),
      StructField("year_founded", IntegerType, true),
      StructField("industry", StringType, true),
      StructField("size_range", StringType, true),
      StructField("country", StringType, true),
      StructField("linkedin_url", StringType, true),
      StructField("current_employee_estimate", IntegerType, true),
      StructField("total_employee_estimate", IntegerType, true)))
    sparkSession.read.option("header", true).schema(transactionSchema).option("mode", "DROPMALFORMED")
  }
}
