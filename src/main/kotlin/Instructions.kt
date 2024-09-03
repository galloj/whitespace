package cz.galloj

import kotlin.math.absoluteValue

class Instruction private constructor(val op: Operation, val param: Int = 0) {
	/**
	 * Transforms the [Instruction] into Whitespace code
	 */
	fun getCode(): String {
		return when(op) {
			Operation.READCH -> "\t\n\t "
			Operation.READI -> "\t\n\t\t"
			Operation.WRITECH -> "\t\n  "
			Operation.WRITEI -> "\t\n \t"
			
			Operation.PUSH -> "  ${codeFromInteger(param)}"
			Operation.DUP -> " \n "
			Operation.SWAP -> " \n\t"
			Operation.POP -> " \n\n"
			Operation.COPY -> " \t ${codeFromInteger(param)}"
			Operation.DROP -> " \t\n${codeFromInteger(param)}"
			
			Operation.ADD -> "\t   "
			Operation.SUB -> "\t  \t"
			Operation.MUL -> "\t  \n"
			Operation.DIV -> "\t \t "
			Operation.MOD -> "\t \t\t"
			
			Operation.LABEL -> "\n  ${codeFromUnsignedInteger(param)}"
			Operation.CALL -> "\n \t${codeFromUnsignedInteger(param)}"
			Operation.JMP -> "\n \n${codeFromUnsignedInteger(param)}"
			Operation.JMPZERO -> "\n\t ${codeFromUnsignedInteger(param)}"
			Operation.JMPNEG -> "\n\t\t${codeFromUnsignedInteger(param)}"
			Operation.RET -> "\n\t\n"
			Operation.EXIT -> "\n\n\n"
			
			Operation.STORE -> "\t\t "
			Operation.LOAD -> "\t\t\t"
		}
	}
	
	/**
	 * Transforms the [Instruction] into human-readable representation
	 */
	fun getRepresentation(): String {
		return when(op) {
			Operation.READCH -> "readch"
			Operation.READI -> "readi"
			Operation.WRITECH -> "writech"
			Operation.WRITEI -> "writei"
			
			Operation.PUSH -> "push $param"
			Operation.DUP -> "dup"
			Operation.SWAP -> "swap"
			Operation.POP -> "pop"
			Operation.COPY -> "copy $param"
			Operation.DROP -> "drop $param"
			
			Operation.ADD -> "add"
			Operation.SUB -> "sub"
			Operation.MUL -> "mul"
			Operation.DIV -> "div"
			Operation.MOD -> "mod"
			
			Operation.LABEL -> "label $param"
			Operation.CALL -> "call $param"
			Operation.JMP -> "jmp $param"
			Operation.JMPZERO -> "jmpzero $param"
			Operation.JMPNEG -> "jmpneg $param"
			Operation.RET -> "ret"
			Operation.EXIT -> "exit"
			
			Operation.STORE -> "store"
			Operation.LOAD -> "load"
		}
	}
	
	override fun equals(other: Any?): Boolean {
		if(other == null) {
			return false
		}
		if(other !is Instruction) {
			return false
		}
		if(this.op != other.op) {
			return false
		}
		if(this.param != other.param) {
			return false
		}
		return true
	}
	
	override fun hashCode(): Int {
		var hash = op.hashCode()
		hash = hash * 31 + param.hashCode()
		return hash
	}
	
	override fun toString(): String {
		return "Instruction($op, $param)"
	}
	
	companion object {
		private fun integerFromCode(code: String): Int {
			if(code.isEmpty()) {
				throw IllegalArgumentException("Integer is missing a sign character")
			}
			if(code[0] == '\n') {
				throw IllegalArgumentException("First character of integer cannot be new line character")
			}
			val isNegative = code[0] == '\t'
			val result = unsignedIntegerFromCode(code.substring(1))
			return if(isNegative) {
				- result
			} else {
				result
			}
		}
		
		private fun codeFromInteger(integer: Int): String {
			return if(integer < 0) {
				"\t"
			} else {
				" "
			} + codeFromUnsignedInteger(integer.absoluteValue)
		}
		
		private fun getIntegerCodeLength(code: String): Int {
			if(code.isEmpty()) {
				throw IllegalArgumentException("Integer is missing a sign character")
			}
			if(code[0] == '\n') {
				throw IllegalArgumentException("First character of integer cannot be new line character")
			}
			return getUnsignedIntegerCodeLength(code.substring(1)) + 1
		}
		
		private fun unsignedIntegerFromCode(code: String): Int {
			var value = 0
			var endFound = false
			for(ch in code) {
				if(ch == '\n') {
					endFound = true
					break
				}
				value *= 2
				if(ch == '\t') {
					value += 1
				}
			}
			if(!endFound) {
				throw IllegalArgumentException("Value is missing the newline end character")
			}
			return value
		}
		
		private fun codeFromUnsignedInteger(integer: Int): String {
			var res = ""
			var remainder = integer
			while(remainder != 0) {
				res = if(remainder and 1 == 1) {
					"\t"
				} else {
					" "
				} + res
				remainder /= 2
			}
			return res + "\n"
		}
		
		private fun getUnsignedIntegerCodeLength(code: String): Int {
			var endFound = false
			var len = 0
			for(ch in code) {
				len += 1
				if(ch == '\n') {
					endFound = true
					break
				}
			}
			if(!endFound) {
				throw IllegalArgumentException("Value is missing the newline end character")
			}
			return len
		}
		
		private fun requireNumberArgument(arg: String?): Int {
			requireNotNull(arg)
			return arg.toInt()
		}
		
		private fun requireLabelArgument(arg: String?): Int {
			requireNotNull(arg)
			val ret = arg.toInt()
			require(ret >= 0)
			return ret
		}
		
		/**
		 * Transforms Whitespace code into new [Instruction]
		 */
		fun fromCode(code: String): Pair<Int, Instruction> {
			if(code.isEmpty()) {
				throw IllegalArgumentException("Not enough data to parse instruction type")
			}
			return if(code.startsWith("\t\n\t ")) {
				Pair(4, readch())
			} else if(code.startsWith("\t\n\t\t")) {
				Pair(4, readi())
			} else if(code.startsWith("\t\n  ")) {
				Pair(4, writech())
			} else if(code.startsWith("\t\n \t")) {
				Pair(4, writei())
			} else if(code.startsWith("  ")) {
				var length = 2
				val param = code.substring(length)
				length += getIntegerCodeLength(param)
				Pair(length, push(integerFromCode(param)))
			} else if(code.startsWith(" \n ")) {
				Pair(3, dup())
			} else if(code.startsWith(" \n\t")) {
				Pair(3, swap())
			} else if(code.startsWith(" \n\n")) {
				Pair(3, pop())
			} else if(code.startsWith(" \t ")) {
				var length = 3
				val param = code.substring(length)
				length += getIntegerCodeLength(param)
				Pair(length, copy(integerFromCode(param)))
			} else if(code.startsWith(" \t\n")) {
				var length = 3
				val param = code.substring(length)
				length += getIntegerCodeLength(param)
				Pair(length, drop(integerFromCode(param)))
			} else if(code.startsWith("\t   ")) {
				Pair(4, add())
			} else if(code.startsWith("\t  \t")) {
				Pair(4, sub())
			} else if(code.startsWith("\t  \n")) {
				Pair(4, mul())
			} else if(code.startsWith("\t \t ")) {
				Pair(4, div())
			} else if(code.startsWith("\t \t\t")) {
				Pair(4, mod())
			} else if(code.startsWith("\n  ")) {
				var length = 3
				val param = code.substring(length)
				length += getUnsignedIntegerCodeLength(param)
				Pair(length, label(unsignedIntegerFromCode(param)))
			} else if(code.startsWith("\n \t")) {
				var length = 3
				val param = code.substring(length)
				length += getUnsignedIntegerCodeLength(param)
				Pair(length, call(unsignedIntegerFromCode(param)))
			} else if(code.startsWith("\n \n")) {
				var length = 3
				val param = code.substring(length)
				length += getUnsignedIntegerCodeLength(param)
				Pair(length, jmp(unsignedIntegerFromCode(param)))
			} else if(code.startsWith("\n\t ")) {
				var length = 3
				val param = code.substring(length)
				length += getUnsignedIntegerCodeLength(param)
				Pair(length, jmpZero(unsignedIntegerFromCode(param)))
			} else if(code.startsWith("\n\t\t")) {
				var length = 3
				val param = code.substring(length)
				length += getUnsignedIntegerCodeLength(param)
				Pair(length, jmpNeg(unsignedIntegerFromCode(param)))
			} else if(code.startsWith("\n\t\n")) {
				Pair(3, ret())
			} else if(code.startsWith("\n\n\n")) {
				Pair(3, exit())
			} else if(code.startsWith("\t\t ")) {
				Pair(3, store())
			} else if(code.startsWith("\t\t\t")) {
				Pair(3, load())
			} else {
				throw IllegalArgumentException("Unknown instruction code")
			}
		}
		
		/**
		 * Transforms human-readable representation generated by [getRepresentation] into new [Instruction]
		 */
		fun fromRepresentation(representation: String): Instruction {
			val parts = representation.split(" ")
			if(parts.size > 2) {
				throw IllegalArgumentException("Instruction is composed at most of single OP code and operand, ${parts.size} parts found")
			}
			val opcode = parts[0].lowercase()
			val argument = parts.getOrNull(1)
			return when(opcode) {
				"readch" -> readch()
				"readi" -> readi()
				"writech" -> writech()
				"writei" -> writei()
				
				"push" -> push(requireNumberArgument(argument))
				"dup" -> dup()
				"swap" -> swap()
				"pop" -> pop()
				"copy" -> copy(requireNumberArgument(argument))
				"drop" -> drop(requireNumberArgument(argument))
				
				"add" -> add()
				"sub" -> sub()
				"mul" -> mul()
				"div" -> div()
				"mod" -> mod()
				
				"label" -> label(requireLabelArgument(argument))
				"call" -> call(requireLabelArgument(argument))
				"jmp" -> jmp(requireLabelArgument(argument))
				"jmpzero" -> jmpZero(requireLabelArgument(argument))
				"jmpneg" -> jmpNeg(requireLabelArgument(argument))
				"ret" -> ret()
				"exit" -> exit()
				
				"store" -> store()
				"load" -> load()
				
				else -> throw IllegalArgumentException("Unknown OP code \"${opcode}\"")
			}
		}
		
		fun readch(): Instruction = Instruction(Operation.READCH)
		fun readi(): Instruction = Instruction(Operation.READI)
		fun writech(): Instruction = Instruction(Operation.WRITECH)
		fun writei(): Instruction = Instruction(Operation.WRITEI)
		
		fun push(value: Int): Instruction = Instruction(Operation.PUSH, value)
		fun dup(): Instruction = Instruction(Operation.DUP)
		fun swap(): Instruction = Instruction(Operation.SWAP)
		fun pop(): Instruction = Instruction(Operation.POP)
		fun copy(stackPosition: Int): Instruction = Instruction(Operation.COPY, stackPosition)
		fun drop(count: Int): Instruction = Instruction(Operation.DROP, count)
		
		fun add(): Instruction = Instruction(Operation.ADD)
		fun sub(): Instruction = Instruction(Operation.SUB)
		fun mul(): Instruction = Instruction(Operation.MUL)
		fun div(): Instruction = Instruction(Operation.DIV)
		fun mod(): Instruction = Instruction(Operation.MOD)
		
		fun label(label: Int): Instruction = Instruction(Operation.LABEL, label)
		fun call(label: Int): Instruction = Instruction(Operation.CALL, label)
		fun jmp(label: Int): Instruction = Instruction(Operation.JMP, label)
		fun jmpZero(label: Int): Instruction = Instruction(Operation.JMPZERO, label)
		fun jmpNeg(label: Int): Instruction = Instruction(Operation.JMPNEG, label)
		fun ret(): Instruction = Instruction(Operation.RET)
		fun exit(): Instruction = Instruction(Operation.EXIT)
		
		fun store(): Instruction = Instruction(Operation.STORE)
		fun load(): Instruction = Instruction(Operation.LOAD)
	}
	
	enum class Operation {
		READCH,
		READI,
		WRITECH,
		WRITEI,
		
		PUSH,
		DUP,
		SWAP,
		POP,
		COPY,
		DROP,
		
		ADD,
		SUB,
		MUL,
		DIV,
		MOD,
		
		LABEL,
		CALL,
		JMP,
		JMPZERO,
		JMPNEG,
		RET,
		EXIT,
		
		STORE,
		LOAD
	}
}