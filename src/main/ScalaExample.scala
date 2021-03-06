import java.nio.file.{Files, Paths}
import java.sql.SQLData

import sun.nio.cs.Surrogate.Parser

import scala.io.Source
import scala.util.Random
import scala.sys.process._
import java.sql.DriverManager

import javax.swing.text.html.HTML
import java.net.URLClassLoader

import com.sun.deploy.net.HttpRequest
import javax.servlet.http.{HttpServletRequest, HttpServletResponse}

class ScalaExample {

  val stringExample = "example";


  // Predictable pseudorandom number generator
  def generateSecretToken() {
    val result = Seq.fill(16)(Random.nextInt)
    return result.map("%02x" format _).mkString
  }

  // Path traversal
  def getWordList(value:String) {
    if (!Files.exists(Paths.get("public/lists/" + value))) {
      NotFound("File not found")
    } else {
      val result = Source.fromFile("public/lists/" + value).getLines().mkString // Weak point
      Ok(result)
    }
  }

  // Potential command injection
  def executeCommand(value:String) {
    val result = value.!
    Ok("Result:\n"+result)
  }

  // Potential Scala Anorm Injection
  val conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/clients","username","password")

  def queryingSQL(value: String): Unit = {
    conn.createStatement().executeQuery("select id, username from people where name = '" + value + "'")
  }

  //  def doGet(value:String) {
  //    Ok("Hello " + value + " !").as("text/html")
  //  }

  def NotFound(str: String): Unit = {
    print(str);
  }

  def Ok(str: String) = {
    print(str);

    def as(arg:String): Unit = {
      print();
    }
  }

/*  // Potential Scala Slick Injection
  db.run {
    sql"select * from people where name = '#$value'".as[Person]
  }

  // Potential Scala Anorm Injection
  val peopleParser = Macro.parser[Person]("id", "name", "age")

  DB.withConnection { implicit c =>
    val people: List[Person] = SQL("select * from people where name = '" + value + "'").as(peopleParser.*)
  }
*/

  // Potential information leakage in Scala Play
  def doGetLeak(value:String) = Action {
    val configElement = configuration.underlying.getString(value)

    Ok("Hello "+ configElement +" !")
  }

/*
  // Scala Play Server-Side Request Forgery (SSRF)
  def doGet(value:String) = Action {
    WS.url(value).get().map { response =>
      Ok(response.body)
    }
  }


  // Potential XSS in Scala Twirl template engine
  @(value: Html)
  <html><h1>bla bla bla</h1></html>
  @value


  // Potential XSS in Scala MVC API engine
  def doGet(value:String) = Action {
    Ok("Hello " + value + " !").as("text/html")
  }*/

  def main(args: Array[String], httpRequest: HttpRequest, httpResponse: HttpServletResponse): Unit = {
    val runtime = Runtime.getRuntime
    //Executes potential dangerous command
    val proc = runtime.exec("find" + " " + args(0))

    executeCommand(args(1))

    readConfig(args(2))

    generateSecretToken()

    getWordList(args(3))

    queryingSQL(args(4))

    doGet2(httpRequest, args(1))

    doGetLeak(args(1))

    cwe494()

    cwe807(httpRequest, httpResponse)

    /*-----------------------*/

    // CWE-190 ----
  val data: Int = Random.nextInt()

  // BAD: may overflow if data is large
  val scaled: Int = data * 10

  // ...

  // GOOD: use a guard to ensure no overflows occur
  var scaled2: Int = 0
  if (data < Integer.MAX_VALUE / 10) {
    scaled2 = data * 10
  }
  else {
    scaled2 = Integer.MAX_VALUE
  }
  // ----- //


  // ---
  val i: Int = 2000000000
  val j: Long = i * i
  // causes overflow
  // --- //

  /*  ------------------------- */

  }

  // CWE-494
  def cwe494(): Unit = {
    val classURLs: Array[Nothing] = Array[Nothing](new Nothing("file:subdir/"))
    val loader = new URLClassLoader(classURLs)
    val loadedClass: Class[_] = Class.forName("loadMe", true, loader)

    loadedClass.getPackage

  }

  def cwe807(request: HttpServletRequest) {

    // CWE-807
    val cookies: Array[Nothing] = request.getCookies()
    var i: Int = 0
    while ( {
      i < cookies.length
    }) {
      val c: Nothing = cookies(i)
      if (c.equals("role")) {
        val usrRole = c.toString
      }

      {
        i += 1; i - 1
      }
    }
  }



  // CWE-732
  def readConfig(configFile: String): Unit = {
    if (!configFile.exists) { // Create an empty config file
      configFile.getBytes
      // Make the file writable for all
      configFile.setWritable(true, false)
    }
    // Now read the config
    Ok(configFile)
  }

  // CWE-79 XSS
  protected def doGet2(req: HttpServletRequest, resp: HttpServletResponse): Unit = {
    val input1 = req.getParameter("input1")
    // ...
    resp.getWriter.write(input1)
  }



}