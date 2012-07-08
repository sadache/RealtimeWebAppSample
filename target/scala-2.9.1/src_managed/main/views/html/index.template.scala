
package views.html

import play.templates._
import play.templates.TemplateMagic._

import play.api.templates._
import play.api.templates.PlayMagic._
import models._
import controllers._
import play.api.i18n._
import play.api.mvc._
import play.api.data._
import views.html._
/**/
object index extends BaseScalaTemplate[play.api.templates.Html,Format[play.api.templates.Html]](play.api.templates.HtmlFormat) with play.api.templates.Template1[String,play.api.templates.Html] {

    /**/
    def apply/*1.2*/(role: String):play.api.templates.Html = {
        _display_ {

Seq[Any](format.raw/*1.16*/("""

"""),_display_(Seq[Any](/*3.2*/main("Stream demonstration")/*3.30*/ {_display_(Seq[Any](format.raw/*3.32*/("""
    
    <h1>Connected as """),_display_(Seq[Any](/*5.23*/role)),format.raw/*5.27*/("""</h1>
    
    <form id="filter">
        Lower bound: <input type="text" value="0" id="min">
        Higher bound: <input type="text" value="1000" id="max">
        <input type="submit" value="Filter">
    </form>
    
    <div id="events"></div>
    
    <script type="text/javascript" charset="utf-8">
    
        var feed;
        
        var open = function(min, max) """),format.raw("""{"""),format.raw/*19.40*/("""
            if(feed) """),format.raw("""{"""),format.raw/*20.23*/("""
                feed.close()
            """),format.raw("""}"""),format.raw/*22.14*/("""
            
            feed = new EventSource('/feed?role="""),_display_(Seq[Any](/*24.49*/role)),format.raw/*24.53*/("""&lower=' + min + '&higher=' + max)

            feed.onmessage = function(e) """),format.raw("""{"""),format.raw/*26.43*/("""
                var data = JSON.parse(e.data)
                if(data.type == 'status') """),format.raw("""{"""),format.raw/*28.44*/("""
                    $('#events').prepend("<p class='status'>STATUS: " + data.message + "</p>")
                """),format.raw("""}"""),format.raw/*30.18*/(""" else if(data.type == 'operation')"""),format.raw("""{"""),format.raw/*30.53*/("""
                    $('#events').prepend("<p class='operation " + data.visibility+ "'>OPERATION: €" + data.amount + "</p>")
                """),format.raw("""}"""),format.raw/*32.18*/("""
            """),format.raw("""}"""),format.raw/*33.14*/("""
            
            $('#events p').addClass('disabled')
        """),format.raw("""}"""),format.raw/*36.10*/("""
        
        $('#filter').submit(function(e) """),format.raw("""{"""),format.raw/*38.42*/("""
            e.preventDefault()
            open($('#min').val(), $('#max').val())
        """),format.raw("""}"""),format.raw/*41.10*/(""").submit()
        
    </script>
    
""")))})))}
    }
    
    def render(role:String) = apply(role)
    
    def f:((String) => play.api.templates.Html) = (role) => apply(role)
    
    def ref = this

}
                /*
                    -- GENERATED --
                    DATE: Mon Jun 18 20:34:25 CEST 2012
                    SOURCE: /Users/monad/Zenexity/QConNY/streams-demo/app/views/index.scala.html
                    HASH: 10d81f04c81735e0d0364f29d342723bcc8cb8c4
                    MATRIX: 505->1|596->15|633->18|669->46|708->48|771->76|796->80|1219->456|1289->479|1379->522|1477->584|1503->588|1628->666|1765->756|1925->869|2007->904|2196->1046|2257->1060|2375->1131|2473->1182|2612->1274
                    LINES: 19->1|22->1|24->3|24->3|24->3|26->5|26->5|40->19|41->20|43->22|45->24|45->24|47->26|49->28|51->30|51->30|53->32|54->33|57->36|59->38|62->41
                    -- GENERATED --
                */
            