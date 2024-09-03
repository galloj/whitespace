import cz.galloj.Instruction
import cz.galloj.Program
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class ProgramTest {
	val testProgram = Program(listOf<Instruction>(
		Instruction.readi(),
		Instruction.push(123),
		Instruction.add(),
		Instruction.writei(),
		Instruction.exit()
	))
	
	@Test
	fun testCodeGeneration() {
		assertEquals(testProgram, Program.fromCode(testProgram.getCode()))
	}
	
	@Test
	fun testRepresentationGeneration() {
		assertEquals(testProgram, Program.fromRepresentation(testProgram.getRepresentation()))
	}
}