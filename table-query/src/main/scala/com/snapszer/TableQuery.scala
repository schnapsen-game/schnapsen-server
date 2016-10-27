package com.snapszer

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.ws._
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Flow
import scala.io.StdIn

object CreateTable extends App{

    implicit val actorSystem = ActorSystem("akka-system")
    implicit val flowMaterializer = ActorMaterializer()
    implicit val executionContext = actorSystem.dispatcher

    val queryHandler: Flow[Message, Message, _] = Flow[Message].map {
      case TextMessage.Strict(txt) => TextMessage("Here is your table data.")
      case _ => TextMessage("Message type unsupported")
    }

    val route = get {
      path("query-table") {
        get {
          handleWebSocketMessages(queryHandler)
        }
      }
    }

    val hostName = "localhost"
    val port = 5678
    val binding = Http().bindAndHandle(route, hostName, port)

    println(s"Queries are now available at ws://$hostName:$port\n")
    StdIn.readLine()

    binding.flatMap(_.unbind()).onComplete(_ => actorSystem.terminate())

}
