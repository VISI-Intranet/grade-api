package collection


import org.mongodb.scala.{Document, MongoClient, MongoCollection, MongoDatabase}
import org.mongodb.scala.model.Filters._
import org.mongodb.scala.model.Updates._



object MongoDBConnection {
private val mongoClient = MongoClient("mongodb://localhost:27017")
  val database: MongoDatabase = mongoClient.getDatabase("UnversityGrade")
  val gradeCollection: MongoCollection[Document] = database.getCollection("Grade")
}
