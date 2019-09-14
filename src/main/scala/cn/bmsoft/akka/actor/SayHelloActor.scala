package cn.bmsoft.akka.actor

import akka.actor.{Actor, ActorRef, ActorSystem, Props}



class SayHelloActor extends Actor {
  //说明
  //1. 当我们继承 Actor 后，就是一个 Actor,核心方法 receive 方法重写
  //1. receive 方法，会被该 Actor 的 MailBox(实现了 Runnable 接口)调用
  // 2. 当该 Actor 的 MailBox 接收到消息,就会调用 receive
  //3. type Receive = PartialFunction[Any, Unit]
  override def receive: Receive = {
    case "hello" =>println("收到 hello, 回应 hello too:)")
    case "ok"=>println("收到 ok, 回应 ok too:)")
    case "exit"=>{
      println("接收到 exit 指令，退出系统")
      context.stop(self)  //停止 actoref
      context.system.terminate()//退出 actorsystem
    }
    case _ =>println("匹配不到")
  }
}

object SayHello{
  //1. 先创建一个 ActorSystem, 专门用于创建 Actor
  private val actoryFactory = ActorSystem("actoryFactory")
  //2. 创建一个 Actor 的同时，返回 Actor 的 ActorRef
  // 说明
  //(1) Props[SayHelloActor] 创建了一个 SayHelloActor 实例，使用反射
  // (2) "sayHelloActor" 给 actor 取名
  //(3) sayHelloActorRef: ActorRef 就是 Props[SayHelloActor] 的 ActorRef
  // (4) 创建的 SayHelloActor 实例被 ActorSystme 接管

  private val sayHelloActor: ActorRef = actoryFactory.actorOf(Props[SayHelloActor],"SayHelloActor")

  def main(args: Array[String]): Unit = {
    sayHelloActor !"hello"
    sayHelloActor !"ok"
    sayHelloActor !"haha"
    sayHelloActor !"exit"


  }



}
