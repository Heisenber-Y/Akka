package cn.bmsoft.akka.sparkmasterworker

import java.util.UUID

import scala.concurrent.duration._
import akka.actor.{Actor, ActorSelection, ActorSystem, Props}
import cn.bmsoft.akka.sparkmasterworker.common.{HeartBeat, RegisterWorkerInfo, RegisteredWorkerInfo, SendHeartBeat}
import com.typesafe.config.ConfigFactory



class SparkWorker(masterHost:String,masterPort:Int,masterName:String) extends Actor{
  //masterProxy是Master的代理/引用ref
  var masterPorxy :ActorSelection = _
  val id=java.util.UUID.randomUUID().toString
  override def preStart(): Unit = {
    println("preStart()调用")
    //初始化masterPorxy
    masterPorxy = context.actorSelection(s"akka.tcp://SparkMaster@${masterHost}:${masterPort}/user/${masterName}")
    println("masterProxy=" + masterPorxy)
  }
  override def receive: Receive = {

    case "start" =>{
      println("worker启动了")
      //发出一个注册消息
      masterPorxy ! RegisterWorkerInfo(id,16,16*1034)
    }
case RegisteredWorkerInfo =>{
  import context.dispatcher
  //说明
  //1. 0 millis 不延时，立即执行定时器
  //2. 3000 millis 表示每隔3秒执行一次
  //3. self:表示发给自己
  //4. SendHeartBeat 发送的内容
  context.system.scheduler.schedule(0 millis, 3000 millis, self, SendHeartBeat)
}
    case SendHeartBeat =>{
      println("worker = " + id + "给master发送心跳")
      masterPorxy ! HeartBeat(id)
    }

  }
}
object SparkWorker {
  def main(args: Array[String]): Unit = {
    if (args.length != 6) {
      println("请输入参数 workerHost workerPort workerName masterHost masterPort masterName")
      sys.exit()
    }

    val workerHost = args(0)
    val workerPort = args(1)
    val workerName = args(2)
    val masterHost = args(3)
    val masterPort = args(4)
    val masterName = args(5)

  /*  val workerHost="127.0.0.1"
    val workerPort=10001
    val masterHost="127.0.0.1"
    val masterPort=10005*/
    val config = ConfigFactory.parseString(
      s"""
         |akka.actor.provider="akka.remote.RemoteActorRefProvider"
         |akka.remote.netty.tcp.hostname=${workerHost}
         |akka.remote.netty.tcp.port=${workerPort}
            """.stripMargin)
  val sparkWorkerSystem = ActorSystem("SparkWorker",config)
    //创建SparkWorker 的引用/代理
    val sparkWorkerRef = sparkWorkerSystem.actorOf(Props(new SparkWorker(masterHost, masterPort.toInt,masterName)),"SparkWorker-01")

    //启动actor
    sparkWorkerRef ! "start"


  }
}
