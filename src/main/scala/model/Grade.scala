package model

case class Grade(gradeId         :String,
                 historyId       :List[String],
                 gradeRK         :String,
                 gradeOtsenka    :Map[String, Int],
                 assignmentType  :String,
                 studentId       :List[String],
                 professorId     :List[String],

                )




