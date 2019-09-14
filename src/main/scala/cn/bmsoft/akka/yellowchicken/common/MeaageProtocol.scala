package cn.bmsoft.akka.yellowchicken.common




  //使用杨例类来构建协议
  //客户端发给服务器协议(序列化的对象)

  case class ClientMessage(str:String)

  //服务端发给客户端的协议(样例类对象)
  case class ServerMessage(mes: String)



