package org.specs2
package reporter

import specification._

private[specs2]
trait ExecutionStrategy {
  val execute: Seq[Seq[Fragment]] => Seq[ExecutedFragment] 
}

private[specs2]
trait DefaultExecutionStrategy extends ExecutionStrategy with FragmentExecution {
  val execute = (fragments: Seq[Seq[Fragment]]) => {
    import scalaz._; import Scalaz._; import concurrent._
    import java.util.concurrent.Executors
    import Strategy.Executor
    implicit val executor = Executors.newFixedThreadPool(4)

    val result = fragments.map(l => l.map(f => promise(executeFragment(f))).sequence.get).flatten
    executor.shutdown()
    result
  }
  
}

private[specs2]
trait SequentialExecutionStrategy extends ExecutionStrategy with FragmentExecution {
  val execute = (fragments: Seq[Seq[Fragment]]) => {
    fragments.map(l => l.map(f => executeFragment(f))).flatten
  }
  
}