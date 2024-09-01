import cz.galloj.Instruction
import kotlin.test.Test
import kotlin.test.assertEquals

class InstructionsTest {
	val instructions = listOf<Instruction>(
		Instruction.readch(),
		Instruction.readi(),
		Instruction.writech(),
		Instruction.writei(),
		
		Instruction.push(5),
		Instruction.dup(),
		Instruction.swap(),
		Instruction.pop(),
		Instruction.copy(3),
		Instruction.drop(7),
		
		Instruction.add(),
		Instruction.sub(),
		Instruction.mul(),
		Instruction.div(),
		Instruction.mod(),
		
		Instruction.label(0),
		Instruction.call(1),
		Instruction.jmp(2),
		Instruction.jmpZero(3),
		Instruction.jmpNeg(4),
		Instruction.ret(),
		Instruction.exit(),
		
		Instruction.store(),
		Instruction.load(),
	)
	
	@Test
	fun testCodeGeneration() {
		instructions.forEach { instruction ->
			assertEquals(instruction, Instruction.fromCode(instruction.getCode()).second)
		}
	}
	
	@Test
	fun testRepresentationGeneration() {
		instructions.forEach { instruction ->
			assertEquals(instruction, Instruction.fromRepresentation(instruction.getRepresentation()))
		}
	}
}