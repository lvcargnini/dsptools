package chisel3.iotesters

import chisel3._

// Bring out a bunch of private functions
object TestersCompatibility {
  def flatten[T <: Aggregate](d: T): IndexedSeq[Bits] = d.flatten
  def getDataNames (name: String, data: Data): Seq[(Element, String)] = getDataNames(name, data)
}



