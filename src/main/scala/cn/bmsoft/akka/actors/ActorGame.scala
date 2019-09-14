package cn.bmsoft.akka.actors

import akka.actor.{ActorRef, ActorSystem, Props}

object ActorGame  extends App {

   val actorfactory = ActorSystem("actorfactory")

  //先创建 BActor 引用/代理

   val bActor: ActorRef = actorfactory.actorOf(Props[BActor],"bActor")
  //创建 AActor 的引用
   val aActor: ActorRef = actorfactory.actorOf(Props(new AActor(bActor)),"aActor")

  //aActor 出招
  aActor! "start"

}
