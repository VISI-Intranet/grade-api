
package repository


import scala.concurrent.Future
import collection.MongoDBConnection
import model._
import org.mongodb.scala.Document
import org.mongodb.scala.bson.{BsonArray, BsonDocument, BsonInt32, BsonString, ObjectId}

import scala.jdk.CollectionConverters._
import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try

object GradeRepository {

  implicit val ec: ExecutionContext = scala.concurrent.ExecutionContext.global

  def getAllGrades(): Future[List[Grade]] = {
    val futureGrades = MongoDBConnection.gradeCollection.find().toFuture()

    futureGrades.map { docs =>
      Option(docs).map { docList =>
        docList.collect { case doc: Document =>
          val studentIdList = Option(doc.get("studentId")).collect {
            case list: java.util.List[BsonString] => list.asScala.map(_.getValue).toList
          }.getOrElse(List.empty[String])


          val gradeOtsenkaMap = Option(doc.get("gradeOtsenka")).collect {
            case map: BsonDocument => map.asScala.map { case (k, v) => k -> v.asInt32().getValue }.toMap
          }.getOrElse(Map.empty[String, Int])



          Grade(
            gradeId = doc.getString("gradeId"),
            historyId = Option(doc.get("historyId")).collect {
              case list: java.util.List[BsonString] => list.asScala.map(_.getValue).toList
            }.getOrElse(List.empty[String]),
            gradeRK = doc.getString("gradeRK"),
            gradeOtsenka = gradeOtsenkaMap,
            assignmentType =doc.getString("assignmentType") ,
            studentId =studentIdList,
            professorId = Option(doc.get("professorId")).collect {
              case list: java.util.List[BsonString] => list.asScala.map(_.getValue).toList
            }.getOrElse(List.empty[String]))
        }.toList // Преобразование Seq в List
      }.getOrElse(List.empty)
    }
  }


  def getGradesById(gradeId: String): Future[Option[Grade]] = {
    val gradeDocument = Document("gradeId" -> gradeId)



    MongoDBConnection.gradeCollection.find(gradeDocument).headOption().map {
      case Some(doc) =>
        val gradeOtsenkaMap = Option(doc.get("gradeOtsenka")).collect {
          case map: BsonDocument => map.asScala.map { case (k, v) => k -> v.asInt32().getValue }.toMap
        }.getOrElse(Map.empty[String, Int])

        Some(
          Grade(
            gradeId = doc.getString("gradeId"),
            historyId =  Option(doc.getList("historyId", classOf[String])).map(_.asScala.toList).getOrElse(List.empty),
            gradeRK = doc.getString("gradeRK"),
            gradeOtsenka =gradeOtsenkaMap ,
            assignmentType = doc.getString("assignmentType"),
            studentId =  Option(doc.getList("studentId", classOf[String])).map(_.asScala.toList).getOrElse(List.empty),
            professorId =  Option(doc.getList("professorId", classOf[String])).map(_.asScala.toList).getOrElse(List.empty)
          )
        )
      case None => None
    }
  }


  def addGrades(grade: Grade): Future[String] = {


    val gradeDocument = BsonDocument(
      "gradeId" -> Option(grade.gradeId),
      "historyId" -> Option(grade.historyId.map(BsonString(_))),
      "gradeRK" ->      BsonString(grade.gradeRK),//BsonDocument("name" -> BsonString(grade.gradeRK.name)),
      "gradeOtsenka" -> BsonDocument(grade.gradeOtsenka.mapValues(BsonInt32(_))) ,
      "assignmentType" -> BsonString(grade.assignmentType),//BsonDocument("name" -> BsonString(grade.assignmentType.name)),
      "studentId" ->  Option(grade.studentId.map(BsonString(_))),
      "professorId" ->  Option(grade.professorId.map(BsonString(_)))
    )

    MongoDBConnection.gradeCollection.insertOne(gradeDocument).toFuture().map(_ => s"Grade with ID ${grade.gradeId} has been added to the database.")
  }
  def deleteGrades(gradeId: String): Future[String] = {
    val gradeDocument = Document("gradeId" -> gradeId)
    MongoDBConnection.gradeCollection.deleteOne(gradeDocument).toFuture().map(_ => s"Grade with id $gradeId has been deleted from the database.")
  }

  def updateGrades(gradeId: String, updatedGrades: Grade): Future[String] = {
    val filter = Document("gradeId" -> gradeId)

    val gradeDocument = BsonDocument(
      "$set" -> BsonDocument(
        "gradeId" -> BsonString(updatedGrades.gradeId),
        "historyId" -> Option(updatedGrades.historyId.map(BsonString(_))),
        "gradeRK" -> BsonString(updatedGrades.gradeRK),
        "gradeOtsenka" -> BsonDocument(updatedGrades.gradeOtsenka.map(kv => kv._1 -> BsonInt32(kv._2))),
        "assignmentType" -> BsonString(updatedGrades.assignmentType),
        "studentId" -> Option(updatedGrades.studentId.map(BsonString(_))),
        "professorId" -> Option(updatedGrades.professorId.map(BsonString(_)))
      )
    )

    MongoDBConnection.gradeCollection.updateOne(filter, gradeDocument).toFuture().map { updatedResult =>
      if (updatedResult.wasAcknowledged() && updatedResult.getModifiedCount > 0) {
        s"Grade with id $gradeId has been updated in the database."
      } else {
        s"Grade update unsuccessful: Either there is an issue in the database or with your input."
      }
    }
  }



  def getGradesByField(param:String): Future[List[Grade]] = {
    val keyValue = param.split("=")

    if (keyValue.length == 2) {
      val key = keyValue(0)
      val value = Try(keyValue(1).toInt).toOption

      val gradeDocument = Document(key -> value)

      MongoDBConnection.gradeCollection.find(gradeDocument).toFuture().map { documents =>
        documents.map { doc =>
          val gradeOtsenkaMap = Option(doc.get("gradeOtsenka")).collect {
            case map: BsonDocument => map.asScala.map { case (k, v) => k -> v.asInt32().getValue }.toMap
          }.getOrElse(Map.empty[String, Int])
          Grade(
            gradeId = doc.getString("gradeId"),
            historyId = Option(doc.getList("historyId", classOf[String])).map(_.asScala.toList).getOrElse(List.empty),
            gradeRK = doc.getString("gradeRK"),
            gradeOtsenka = gradeOtsenkaMap,
            assignmentType = doc.getString("assignmentType"),
            studentId = Option(doc.getList("studentId", classOf[String])).map(_.asScala.toList).getOrElse(List.empty),
            professorId = Option(doc.getList("professorId", classOf[String])).map(_.asScala.toList).getOrElse(List.empty),
          )
        }.toList
      }
    }

    else {
      // Обработка некорректного ввода
      Future.failed(new IllegalArgumentException("Неверный формат параметра"))
    }
  }
}
