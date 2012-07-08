// @SOURCE:/Users/monad/Zenexity/QConNY/streams-demo/conf/routes
// @HASH:3af2808739cf378111691d05bad1a70bda6e2a08
// @DATE:Mon Jun 18 20:34:24 CEST 2012


import play.core._
import play.core.Router._
import play.core.j._

import play.api.mvc._


import Router.queryString


// @LINE:12
// @LINE:9
// @LINE:7
// @LINE:6
package controllers {

// @LINE:9
// @LINE:7
// @LINE:6
class ReverseApplication {
    


 
// @LINE:7
// @LINE:6
def index(role:String) = {
   (role) match {
// @LINE:6
case (role) if role == "EMPLOYEE" => Call("GET", Routes.prefix)
                                                                
// @LINE:7
case (role) if role == "MANAGER" => Call("GET", Routes.prefix + { if(Routes.prefix.endsWith("/")) "" else "/" } + "manager")
                                                                    
   }
}
                                                        
 
// @LINE:9
def feed(role:String, lower:Int, higher:Int) = {
   Call("GET", Routes.prefix + { if(Routes.prefix.endsWith("/")) "" else "/" } + "feed" + queryString(List(Some(implicitly[QueryStringBindable[String]].unbind("role", role)), Some(implicitly[QueryStringBindable[Int]].unbind("lower", lower)), Some(implicitly[QueryStringBindable[Int]].unbind("higher", higher)))))
}
                                                        

                      
    
}
                            

// @LINE:12
class ReverseAssets {
    


 
// @LINE:12
def at(file:String) = {
   Call("GET", Routes.prefix + { if(Routes.prefix.endsWith("/")) "" else "/" } + "assets/" + implicitly[PathBindable[String]].unbind("file", file))
}
                                                        

                      
    
}
                            
}
                    


// @LINE:12
// @LINE:9
// @LINE:7
// @LINE:6
package controllers.javascript {

// @LINE:9
// @LINE:7
// @LINE:6
class ReverseApplication {
    


 
// @LINE:7
// @LINE:6
def index = JavascriptReverseRoute(
   "controllers.Application.index",
   """
      function(role) {
      if (role == """ + implicitly[JavascriptLitteral[String]].to("EMPLOYEE") + """) {
      return _wA({method:"GET", url:"""" + Routes.prefix + """"})
      }
      if (role == """ + implicitly[JavascriptLitteral[String]].to("MANAGER") + """) {
      return _wA({method:"GET", url:"""" + Routes.prefix + { if(Routes.prefix.endsWith("/")) "" else "/" } + """" + "manager"})
      }
      }
   """
)
                                
 
// @LINE:9
def feed = JavascriptReverseRoute(
   "controllers.Application.feed",
   """
      function(role,lower,higher) {
      return _wA({method:"GET", url:"""" + Routes.prefix + { if(Routes.prefix.endsWith("/")) "" else "/" } + """" + "feed" + _qS([(""" + implicitly[QueryStringBindable[String]].javascriptUnbind + """)("role", role), (""" + implicitly[QueryStringBindable[Int]].javascriptUnbind + """)("lower", lower), (""" + implicitly[QueryStringBindable[Int]].javascriptUnbind + """)("higher", higher)])})
      }
   """
)
                                

                      
    
}
                

// @LINE:12
class ReverseAssets {
    


 
// @LINE:12
def at = JavascriptReverseRoute(
   "controllers.Assets.at",
   """
      function(file) {
      return _wA({method:"GET", url:"""" + Routes.prefix + { if(Routes.prefix.endsWith("/")) "" else "/" } + """" + "assets/" + (""" + implicitly[PathBindable[String]].javascriptUnbind + """)("file", file)})
      }
   """
)
                                

                      
    
}
                
}
          


// @LINE:12
// @LINE:9
// @LINE:7
// @LINE:6
package controllers.ref {

// @LINE:9
// @LINE:7
// @LINE:6
class ReverseApplication {
    


 
// @LINE:6
def index(role:String) = new play.api.mvc.HandlerRef(
   controllers.Application.index(role), HandlerDef(this, "controllers.Application", "index", Seq(classOf[String]))
)
                              
 
// @LINE:9
def feed(role:String, lower:Int, higher:Int) = new play.api.mvc.HandlerRef(
   controllers.Application.feed(role, lower, higher), HandlerDef(this, "controllers.Application", "feed", Seq(classOf[String], classOf[Int], classOf[Int]))
)
                              

                      
    
}
                            

// @LINE:12
class ReverseAssets {
    


 
// @LINE:12
def at(path:String, file:String) = new play.api.mvc.HandlerRef(
   controllers.Assets.at(path, file), HandlerDef(this, "controllers.Assets", "at", Seq(classOf[String], classOf[String]))
)
                              

                      
    
}
                            
}
                    
        