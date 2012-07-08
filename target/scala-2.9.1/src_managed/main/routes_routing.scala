// @SOURCE:/Users/monad/Zenexity/QConNY/streams-demo/conf/routes
// @HASH:3af2808739cf378111691d05bad1a70bda6e2a08
// @DATE:Mon Jun 18 20:34:24 CEST 2012


import play.core._
import play.core.Router._
import play.core.j._

import play.api.mvc._


import Router.queryString

object Routes extends Router.Routes {

private var _prefix = "/"

def setPrefix(prefix: String) {
  _prefix = prefix  
  List[(String,Routes)]().foreach {
    case (p, router) => router.setPrefix(prefix + (if(prefix.endsWith("/")) "" else "/") + p)
  }
}

def prefix = _prefix


// @LINE:6
lazy val controllers_Application_index0 = Route("GET", PathPattern(List(StaticPart(Routes.prefix))))
          

// @LINE:7
lazy val controllers_Application_index1 = Route("GET", PathPattern(List(StaticPart(Routes.prefix),StaticPart(if(Routes.prefix.endsWith("/")) "" else "/"),StaticPart("manager"))))
          

// @LINE:9
lazy val controllers_Application_feed2 = Route("GET", PathPattern(List(StaticPart(Routes.prefix),StaticPart(if(Routes.prefix.endsWith("/")) "" else "/"),StaticPart("feed"))))
          

// @LINE:12
lazy val controllers_Assets_at3 = Route("GET", PathPattern(List(StaticPart(Routes.prefix),StaticPart(if(Routes.prefix.endsWith("/")) "" else "/"),StaticPart("assets/"),DynamicPart("file", """.+"""))))
          
def documentation = List(("""GET""", prefix,"""controllers.Application.index(role:String = "EMPLOYEE")"""),("""GET""", prefix + (if(prefix.endsWith("/")) "" else "/") + """manager""","""controllers.Application.index(role:String = "MANAGER")"""),("""GET""", prefix + (if(prefix.endsWith("/")) "" else "/") + """feed""","""controllers.Application.feed(role:String, lower:Int, higher:Int)"""),("""GET""", prefix + (if(prefix.endsWith("/")) "" else "/") + """assets/$file<.+>""","""controllers.Assets.at(path:String = "/public", file:String)""")).foldLeft(List.empty[(String,String,String)]) { (s,e) => e match {
  case r @ (_,_,_) => s :+ r.asInstanceOf[(String,String,String)]
  case l => s ++ l.asInstanceOf[List[(String,String,String)]] 
}}
         
    
def routes:PartialFunction[RequestHeader,Handler] = {        

// @LINE:6
case controllers_Application_index0(params) => {
   call(Param[String]("role", Right("EMPLOYEE"))) { (role) =>
        invokeHandler(_root_.controllers.Application.index(role), HandlerDef(this, "controllers.Application", "index", Seq(classOf[String])))
   }
}
          

// @LINE:7
case controllers_Application_index1(params) => {
   call(Param[String]("role", Right("MANAGER"))) { (role) =>
        invokeHandler(_root_.controllers.Application.index(role), HandlerDef(this, "controllers.Application", "index", Seq(classOf[String])))
   }
}
          

// @LINE:9
case controllers_Application_feed2(params) => {
   call(params.fromQuery[String]("role", None), params.fromQuery[Int]("lower", None), params.fromQuery[Int]("higher", None)) { (role, lower, higher) =>
        invokeHandler(_root_.controllers.Application.feed(role, lower, higher), HandlerDef(this, "controllers.Application", "feed", Seq(classOf[String], classOf[Int], classOf[Int])))
   }
}
          

// @LINE:12
case controllers_Assets_at3(params) => {
   call(Param[String]("path", Right("/public")), params.fromPath[String]("file", None)) { (path, file) =>
        invokeHandler(_root_.controllers.Assets.at(path, file), HandlerDef(this, "controllers.Assets", "at", Seq(classOf[String], classOf[String])))
   }
}
          
}
    
}
          