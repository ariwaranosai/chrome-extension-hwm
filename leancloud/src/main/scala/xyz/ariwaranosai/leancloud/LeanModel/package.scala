package xyz.ariwaranosai.leancloud
/**
  * LeanModel provides default model case class for leancloud request.
  */

import io.circe.generic.auto._
import io.circe.parser._
import io.circe.syntax._
import cats.data.Xor._
import io.circe.{Decoder, Error}

import scala.concurrent.Future
import scalajs.concurrent.JSExecutionContext.Implicits.queue


package object LeanModel {

  def toLeanInternalException[T](origin: String): Future[T] =
    Future {
      decode[LeanErrorResponse](origin) match {
        case Right(x) => throw LeanInternalException(x.code, x.error)
        case Left(x) => throw LeanJsonParserException(origin, x.getMessage)
      }
    }

  /**
    * object LeanModelImplicit provides implicit function to
    * convent result json to class.
    */
  object LeanModelImplicit {
    implicit def idResponse(s: String): Future[String] = Future {s}

    implicit def createModel[T](s: String)(implicit decoder: Decoder[T]): Future[T] = Future {
      decode[T](s) match {
        case Right(x) => x
        case Left(x) => throw LeanJsonParserException(s, x.toString)
      }
    }

    implicit def createResponse(s: String): Future[CreateResponse] =
      createModel[CreateResponse](s)

    implicit def UserCreateResponse(s: String): Future[UserCreateResponse] =
      createModel[UserCreateResponse](s)

    implicit def NoneResponse(s: String): Future[NoneResult] =
      createModel[NoneResult](s)

    implicit def updateResponse(s: String): Future[UpdateResponse] =
      createModel[UpdateResponse](s)

    implicit def sting2Model[A](input: String)(implicit decoder: Decoder[A]): Future[A] = Future {
      parse(input).flatMap(decoder.decodeJson) match {
        case Right(x) => x
        case Left(x) => throw LeanJsonParserException(input, x.toString)
      }
    }
  }
}
