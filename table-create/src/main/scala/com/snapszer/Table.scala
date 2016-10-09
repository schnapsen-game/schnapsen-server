package com.snapszer

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.ws.{Message, TextMessage}
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Flow
import scala.io.StdIn

object CreateTable extends App{

    implicit val actorSystem = ActorSystem("akka-system")
    implicit val flowMaterializer = ActorMaterializer()
    implicit val executionContext = actorSystem.dispatcher

    def createTable(tableName: String) = {
      TextMessage(s"Table with $tableName created.")
    }

    val creatorHandler: Flow[Message, Message, _] = Flow[Message].map {
      case TextMessage.Strict(txt) => createTable(txt)
      case _ => TextMessage("Message type unsupported")
    }

    val route = get {
      path("create-table") {
        get {
          handleWebSocketMessages(creatorHandler)
        }
      }
    }

    val hostName = "localhost"
    val port = 8080
    val binding = Http().bindAndHandle(route, hostName, port)

    println(s"Server is now online at http://$hostName:$port\nPress RETURN to stop...")
    StdIn.readLine()

    binding.flatMap(_.unbind()).onComplete(_ => actorSystem.terminate())

}
