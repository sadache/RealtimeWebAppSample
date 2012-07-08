package controllers

import play.api._
import play.api.mvc._

import play.api.libs._
import play.api.libs.json._
import play.api.libs.iteratee._
import play.api.libs.concurrent._

import play.api.libs.json.Json._

import models._

object Application extends Controller {
  
  def index(role: String) = Action {
    Ok(views.html.index(role))
  }
  
  def feed(role: String, lowerBound: Int, higherBound: Int) = Action {
    
    val secure: Enumeratee[Event, Event] = Enumeratee.collect[Event] {
      case e@SystemStatus(_) if role == "MANAGER" => e
      case e@Operation("private", _) if role == "MANAGER" => e
      case e@Operation("public", _) => e
    }
    
    val inBounds: Enumeratee[Event, Event] = Enumeratee.collect[Event] {
      case e@Operation(_, amout) if amout > lowerBound && amout < higherBound => e
      case e@SystemStatus(_) => e
    }
    
    val asJson: Enumeratee[Event, JsValue] = Enumeratee.map[Event] { 
      case Operation(visibility, amount) => toJson(Map("type" -> toJson("operation"), "amount" -> toJson(amount), "visibility" -> toJson(visibility)))
      case SystemStatus(msg) => toJson(Map("type" -> "status", "message" -> msg))
    }
    
    val finalAdpater = secure ><> inBounds ><> asJson
    
    Ok.feed(Stream.events &> finalAdpater ><> EventSource()).as("text/event-stream")

  }
  
}