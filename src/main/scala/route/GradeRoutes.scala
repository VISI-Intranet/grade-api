package route

import akka.http.scaladsl.server.Directives._
import de.heikoseeberger.akkahttpjson4s.Json4sSupport
import org.json4s.{DefaultFormats, jackson}
import repository.GradeRepository
import model.Grade

object GradeRoutes extends Json4sSupport {
  implicit val serialization = jackson.Serialization
  implicit val formats = DefaultFormats

  val route =
    pathPrefix("grade") {
      concat(
        get {
          parameter("param") { param =>
            complete(GradeRepository.getGradesByField(param.toString))
          }
        },
        pathEnd {
          concat(
            get {
              complete(GradeRepository.getAllGrades())
            },
            post {
              entity(as[Grade]) { grade =>
                complete(GradeRepository.addGrades(grade))
              }
            }
          )
        },
        path(Segment) { gradeId =>
          concat(
            get {
              complete(GradeRepository.getGradesById(gradeId))
            },
            put {
              entity(as[Grade]) { updatedGrade =>
                complete(GradeRepository.updateGrades(gradeId, updatedGrade))
              }
            },
            delete {
              complete(GradeRepository.deleteGrades(gradeId))
            }
          )
        }
      )
    }
}




