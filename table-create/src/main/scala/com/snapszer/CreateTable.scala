package com.snapszer

import akka.{ Done, NotUsed }
import akka.actor.ActorSystem
import akka.http.scaladsl._
import akka.http.scaladsl.model._
import akka.http.scaladsl.model.ws._
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import akka.stream.scaladsl._
import scala.io.StdIn
import akka.actor.ActorSystem
import akka.{ Done, NotUsed }

import scala.concurrent.Future

object CreateTable extends App{

    implicit val actorSystem = ActorSystem("akka-system")
    implicit val flowMaterializer = ActorMaterializer()
    implicit val executionContext = actorSystem.dispatcher

    val printSink: Sink[Message, Future[Done]] =
        Sink.foreach {
            case message: TextMessage.Strict =>
                println(message.text)
        }

    val helloSource: Source[Message, NotUsed] =
        Source.single(TextMessage("hello world!"))

    val flow: Flow[Message, Message, Future[Done]] =
        Flow.fromSinkAndSourceMat(printSink, helloSource)(Keep.left)

    def sendTableNotification(tableName: String) = {
        val queryTable = WebSocketRequest("ws://localhost:5678/query-table")
        val webSocketFlow = Http().singleWebSocketRequest(queryTable, flow)
        val (upgradeResponse, closed) =
            Http().singleWebSocketRequest(queryTable, flow)
        val connected = upgradeResponse.map { upgrade =>
            if (upgrade.response.status == StatusCodes.SwitchingProtocols) {
                Done
            } else {
                throw new RuntimeException(s"Connection failed: ${upgrade.response.status}")
            }
        }
      1
    }

    def createTable(tableName: String) = {
      sendTableNotification(tableName)
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

    println(s"Server is now online at ws://$hostName:$port\nPress RETURN to stop...")
    StdIn.readLine()

    binding.flatMap(_.unbind()).onComplete(_ => actorSystem.terminate())

}
 
