package cz.galloj

import kotlin.math.absoluteValue

class Instruction private constructor(val op: Operation, val param: Int = 0) {
	fun getCode(): String {
		return when(op) {
			Operation.READCH -> "\t \t "
			Operation.READI -> "\t \t\t"
			Operation.WRITECH -> "\t   "
			Operation.WRITEI -> "\t  \t"
			
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
		
		fun fromCode(code: String): Pair<Int, Instruction> {
			if(code.isEmpty()) {
				throw IllegalArgumentException("Not enough data to parse instruction type")
			}
			return if(code.startsWith("\t\n\t ")) {
				Pair(4, Instruction(Operation.READCH))
			} else if(code.startsWith("\t\n\t\t")) {
				Pair(4, Instruction(Operation.READI))
			} else if(code.startsWith("\t\n  ")) {
				Pair(4, Instruction(Operation.WRITECH))
			} else if(code.startsWith("\t\n \t")) {
				Pair(4, Instruction(Operation.WRITEI))
			} else if(code.startsWith("  ")) {
				var length = 2
				val param = code.substring(length)
				length += getIntegerCodeLength(param)
				Pair(length, Instruction(Operation.PUSH, integerFromCode(param)))
			} else if(code.startsWith(" \n ")) {
				Pair(3, Instruction(Operation.DUP))
			} else if(code.startsWith(" \n\t")) {
				Pair(3, Instruction(Operation.SWAP))
			} else if(code.startsWith(" \n\n")) {
				Pair(3, Instruction(Operation.POP))
			} else if(code.startsWith(" \t ")) {
				var length = 3
				val param = code.substring(length)
				length += getIntegerCodeLength(param)
				Pair(length, Instruction(Operation.COPY, integerFromCode(param)))
			} else if(code.startsWith(" \t\n")) {
				var length = 3
				val param = code.substring(length)
				length += getIntegerCodeLength(param)
				Pair(length, Instruction(Operation.DROP, integerFromCode(param)))
			} else if(code.startsWith("\t   ")) {
				Pair(4, Instruction(Operation.ADD))
			} else if(code.startsWith("\t  \t")) {
				Pair(4, Instruction(Operation.SUB))
			} else if(code.startsWith("\t  \n")) {
				Pair(4, Instruction(Operation.MUL))
			} else if(code.startsWith("\t \t ")) {
				Pair(4, Instruction(Operation.DIV))
			} else if(code.startsWith("\t \t\t")) {
				Pair(4, Instruction(Operation.MOD))
			} else if(code.startsWith("\n  ")) {
				var length = 3
				val param = code.substring(length)
				length += getUnsignedIntegerCodeLength(param)
				Pair(length, Instruction(Operation.LABEL, unsignedIntegerFromCode(param)))
			} else if(code.startsWith("\n \t")) {
				var length = 3
				val param = code.substring(length)
				length += getUnsignedIntegerCodeLength(param)
				Pair(length, Instruction(Operation.CALL, unsignedIntegerFromCode(param)))
			} else if(code.startsWith("\n \n")) {
				var length = 3
				val param = code.substring(length)
				length += getUnsignedIntegerCodeLength(param)
				Pair(length, Instruction(Operation.JMP, unsignedIntegerFromCode(param)))
			} else if(code.startsWith("\n\t ")) {
				var length = 3
				val param = code.substring(length)
				length += getUnsignedIntegerCodeLength(param)
				Pair(length, Instruction(Operation.JMPZERO, unsignedIntegerFromCode(param)))
			} else if(code.startsWith("\n\t\t")) {
				var length = 3
				val param = code.substring(length)
				length += getUnsignedIntegerCodeLength(param)
				Pair(length, Instruction(Operation.JMPNEG, unsignedIntegerFromCode(param)))
			} else if(code.startsWith("\n\t\n")) {
				Pair(3, Instruction(Operation.RET))
			} else if(code.startsWith("\n\n\n")) {
				Pair(3, Instruction(Operation.EXIT))
			} else if(code.startsWith("\t\t ")) {
				Pair(3, Instruction(Operation.STORE))
			} else if(code.startsWith("\t\t\t")) {
				Pair(3, Instruction(Operation.LOAD))
			} else {
				throw IllegalArgumentException("Unknown instruction code")
			}
		}
		
		fun fromRepresentation(representation: String): Instruction {
			val parts = representation.split(" ")
			if(parts.size > 2) {
				throw IllegalArgumentException("Instruction is composed at most of single OP code and operand, ${parts.size} parts found")
			}
			val opcode = parts[0].lowercase()
			val argument = parts.getOrNull(1)
			return when(opcode) {
				"readch" -> Instruction(Operation.READCH)
				"readi" -> Instruction(Operation.READI)
				"writech" -> Instruction(Operation.WRITECH)
				"writei" -> Instruction(Operation.WRITEI)
				
				"push" -> Instruction(Operation.PUSH, requireNumberArgument(argument))
				"dup" -> Instruction(Operation.DUP)
				"swap" -> Instruction(Operation.SWAP)
				"pop" -> Instruction(Operation.POP)
				"copy" -> Instruction(Operation.COPY, requireNumberArgument(argument))
				"drop" -> Instruction(Operation.DROP, requireNumberArgument(argument))
				
				"add" -> Instruction(Operation.ADD)
				"sub" -> Instruction(Operation.SUB)
				"mul" -> Instruction(Operation.MUL)
				"div" -> Instruction(Operation.DIV)
				"mod" -> Instruction(Operation.MOD)
				
				"label" -> Instruction(Operation.LABEL, requireLabelArgument(argument))
				"call" -> Instruction(Operation.CALL, requireLabelArgument(argument))
				"jmp" -> Instruction(Operation.JMP, requireLabelArgument(argument))
				"jmpzero" -> Instruction(Operation.JMPZERO, requireLabelArgument(argument))
				"jmpneg" -> Instruction(Operation.JMPNEG, requireLabelArgument(argument))
				"ret" -> Instruction(Operation.RET)
				"exit" -> Instruction(Operation.EXIT)
				
				"store" -> Instruction(Operation.STORE)
				"load" -> Instruction(Operation.LOAD)
				
				else -> throw IllegalArgumentException("Unknown OP code \"${opcode}\"")
			}
		}
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