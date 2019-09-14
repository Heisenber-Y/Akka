package cn.bmsoft.akka.yellowchicken.client

import akka.actor.{Actor, ActorRef, ActorSelection, ActorSystem, Props}
import cn.bmsoft.akka.yellowchicken.common.{ClientMessage, ServerMessage}
import com.typesafe.config.ConfigFactory

import scala.io.StdIn



class CustomerActor(serverHost:String,serverPort:Int) extends Actor {
  //定义一个YellowChickenServerRef
  var serverActorRef: ActorSelection = _

  //在Actor中有一个方法PreStart方法，他会在actor运行前执行
  //在akka的开发中，通常将初始化的工作，放在preStart方法
  override def preStart(): Unit = {
    println("preStart() 执行")
    serverActorRef = context.actorSelection(s"akka.tcp://Server@${serverHost}:${serverPort}/user/YellowChickenServer")
    println("serverActorRef=" + serverActorRef)

  }

  override def receive: Receive = {

    case "start" => println("客户端start ，可以咨询问题")
    case str: String => {
      //发给小黄鸡可客服人员
      //使用ClientMessage case class apply
      serverActorRef ! ClientMessage(str)
    }
    case ServerMessage(mes) => {
      println("收到小黄鸡客服的消息"+mes)
    }
  }

  //入口阿斯顿史蒂夫

}
object CustomerActor extends App{

  val (clientHost, clientPort, serverHost, serverPort) = ("127.0.0.1", 9990, "127.0.0.1", 9999)
  val config = ConfigFactory.parseString(
    s"""
       |akka.actor.provider="akka.remote.RemoteActorRefProvider"
       |akka.remote.netty.tcp.hostname=$clientHost
       |akka.remote.netty.tcp.port=$clientPort
        """.stripMargin)

  //创建ActorSystem
  val clientActorSystem = ActorSystem("client", config)
   val customerActorRef: ActorRef = clientActorSystem.actorOf(Props(new CustomerActor(serverHost,serverPort)),"CustomerActor")
  //启动customerRef/也可以理解启动Actor
  customerActorRef! "start"
  while (true){
    println("请输入要咨询的问题")
    val str = StdIn.readLine()
    customerActorRef ! str
  }


}