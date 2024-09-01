package cz.galloj

import cz.galloj.Instruction.Operation

class MachineState {
	val heap: HashMap<Int, Int> = HashMap()
	val stack: MutableList<Int> = ArrayList()
	val returnAddresses: MutableList<Int> = ArrayList()
	var pc: Int = 0
	var terminated: Boolean = false
}

interface IOInterface {
	fun getInt(): Int
	fun getChar(): Char
	fun putInt(arg: Int)
	fun putChar(arg: Char)
}

class Machine(var program: Program, var ioInterface: IOInterface) {
	var machineState = MachineState()
	
	fun isFinished(): Boolean {
		return machineState.terminated
	}
	
	private fun getStackItem(): Int {
		if(machineState.stack.isEmpty()) {
			throw IllegalStateException("Unable to remove element from empty stack")
		}
		val res = machineState.stack.last()
		machineState.stack.removeLast()
		return res
	}
	
	private fun pushStackItem(item: Int) {
		machineState.stack.add(item)
	}
	
	private fun findLabelInstructionId(label: Int): Int {
		for((id, instruction) in program.instructions.withIndex()) {
			if(instruction.op == Operation.LABEL && instruction.param == label) {
				return id
			}
		}
		throw IllegalStateException("The label $label was not found")
	}
	
	fun step() {
		var branchTaken = false
		if(machineState.pc >= program.instructions.size) {
			throw IllegalStateException("The PC is out of valid instruction address range")
		}
		val currentInstruction = program.instructions[machineState.pc]
		when(currentInstruction.op) {
			Operation.READCH -> {
				machineState.heap[getStackItem()] = ioInterface.getChar().code
			}
			Operation.READI -> {
				machineState.heap[getStackItem()] = ioInterface.getInt()
			}
			Operation.WRITECH -> {
				ioInterface.putChar(getStackItem().toChar())
			}
			Operation.WRITEI -> {
				ioInterface.putInt(getStackItem())
			}
			
			Operation.PUSH -> {
				pushStackItem(currentInstruction.param)
			}
			Operation.DUP -> {
				val item = getStackItem()
				pushStackItem(item)
				pushStackItem(item)
			}
			Operation.SWAP -> {
				val item1 = getStackItem()
				val item2 = getStackItem()
				pushStackItem(item1)
				pushStackItem(item2)
			}
			Operation.POP -> {
				getStackItem()
			}
			Operation.COPY -> {
				pushStackItem(machineState.stack[machineState.stack.size - currentInstruction.param - 1])
			}
			Operation.DROP -> {
				(0..<currentInstruction.param).forEach {
					machineState.stack.remove(machineState.stack.size - 2)
				}
			}
			
			Operation.ADD -> {
				pushStackItem(getStackItem() + getStackItem())
			}
			Operation.SUB -> {
				val b = getStackItem()
				val a = getStackItem()
				pushStackItem(a - b)
			}
			Operation.MUL -> {
				pushStackItem(getStackItem() * getStackItem())
			}
			Operation.DIV -> {
				val b = getStackItem()
				val a = getStackItem()
				pushStackItem(a / b)
			}
			Operation.MOD -> {
				val b = getStackItem()
				val a = getStackItem()
				pushStackItem(a % b)
			}
			
			Operation.LABEL -> {
				// does nothing
			}
			Operation.CALL -> {
				machineState.returnAddresses.add(machineState.pc + 1)
				machineState.pc = findLabelInstructionId(currentInstruction.param)
				branchTaken = true
			}
			Operation.JMP -> {
				machineState.pc = findLabelInstructionId(currentInstruction.param)
				branchTaken = true
			}
			Operation.JMPZERO -> {
				if(getStackItem() == 0) {
					machineState.pc = findLabelInstructionId(currentInstruction.param)
					branchTaken = true
				}
			}
			Operation.JMPNEG -> {
				if(getStackItem() < 0) {
					machineState.pc = findLabelInstructionId(currentInstruction.param)
					branchTaken = true
				}
			}
			Operation.RET -> {
				machineState.pc = machineState.returnAddresses.last()
				machineState.returnAddresses.removeLast()
				branchTaken = true
			}
			Operation.EXIT -> {
				machineState.terminated = true
			}
			
			Operation.STORE -> {
				val value = getStackItem()
				val address = getStackItem()
				machineState.heap[address] = value
			}
			Operation.LOAD -> {
				pushStackItem(machineState.heap.getOrDefault(getStackItem(), 0))
			}
		}
		if(!branchTaken) {
			machineState.pc += 1
		}
	}
	
	fun run() {
		while(!isFinished()) {
			step()
		}
	}
}