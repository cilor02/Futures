package com.milo.futures

import scala.concurrent.future
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import scala.io.Source
import java.io.File
import java.util.ArrayList
import scala.util.Success
import scala.util.Failure
import scala.util.Failure

object MyFutures {
  
  def topLevelDirs (root: String): Future[List[File]] =
    future {
    val dir = new File(root)
    val contents = dir listFiles()
    val folders = contents.filter(_.isDirectory()) 
    folders toList
  }
  
  def asyncFindFiles (folder : File): Future[List[File]] = 
    future {
    folder listFiles() filter (_.isFile()) foreach(println)
    folder listFiles() filter (_.isFile()) toList
  }
  
 def findFiles (folder : File): List[File] = 
     {
    folder listFiles() filter (_.isFile()) foreach(println)
    folder listFiles() filter (_.isFile()) toList
  }
  
    def countFiles (folder : File): Int = {
    
    folder listFiles() filter (_.isFile()) foreach(println)
    (folder listFiles() filter (_.isFile()) toList).size
  }
    
  def asyncCountFiles (folder : File): Future[Int] = future{
    
    folder listFiles() filter (_.isFile()) foreach(println)
    (folder listFiles() filter (_.isFile()) toList).size
  }
  
  def doitAll (root : String): Future[List[Int]] = {
    
    topLevelDirs(root).map {
     listDirs => { 
       listDirs foreach println;
       listDirs map countFiles
     }  
    }
    
  }
  
    def doitAllNested (root : String): Future[Future[List[Int]]] = {
    
    topLevelDirs(root).map {
     listDirs => { 
       listDirs foreach println;
       Future.sequence(listDirs map asyncCountFiles).map(x=>x)
     }  
    }
    
  }
    
   def doitAllFlat (root : String): Future[List[Int]] = {
    
    topLevelDirs(root).flatMap {
     listDirs => { 
       Future.sequence(listDirs map asyncCountFiles).map(x=>x)
     }  
    }
    
  }
  
  def main (args : Array[String]):Unit = 
  {
     topLevelDirs("C:\\").map{ folders => folders map findFiles }.foreach(println)
     
     val f = topLevelDirs("C:\\").flatMap{ folders => Future.sequence(folders map asyncFindFiles) }
     
     f.onComplete
     {
       case Success (list) => list foreach println
       case Failure (ex) =>  println (ex)
     }
     Thread.sleep(5000)
  }
}