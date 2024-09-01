package cz.galloj

class Program(val instructions: List<Instruction>) {
	fun getCode(): String {
		val sb = StringBuilder()
		for(instruction in instructions) {
			sb.append(instruction.getCode())
		}
		return sb.toString()
	}
	
	fun getRepresentation(): String {
		val sb = StringBuilder()
		for(instruction in instructions) {
			sb.append(instruction.getRepresentation())
			sb.append('\n')
		}
		return sb.toString()
	}
	
	companion object {
		fun fromCode(code: String): Program {
			val codeWithoutComments = code.replace(Regex("[^ \t\n]"), "")
			var index = 0
			val instructions = mutableListOf<Instruction>()
			while(index < codeWithoutComments.length) {
				val (parsedLength, newInstruction) = Instruction.fromCode(codeWithoutComments.substring(index))
				index += parsedLength
				instructions.add(newInstruction)
			}
			return Program(instructions)
		}
		
		fun fromRepresentation(representation: String): Program {
			val lines = representation.split("\n").toMutableList()
			if(lines.isNotEmpty() && lines.last().isEmpty()) {
				lines.removeLast()
			}
			val instructions = mutableListOf<Instruction>()
			for(line in lines) {
				instructions.add(Instruction.fromRepresentation(line))
			}
			return Program(instructions)
		}
	}
}