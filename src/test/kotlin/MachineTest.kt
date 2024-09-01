import cz.galloj.IOInterface
import cz.galloj.Instruction
import cz.galloj.Machine
import cz.galloj.Program
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.fail

class MachineTest {
	fun executeProgram(program: Program): String {
		var result = ""
		val machine = Machine(program, object: IOInterface {
			override fun getInt(): Int {
				fail()
			}
			
			override fun getChar(): Char {
				fail()
			}
			
			override fun putInt(arg: Int) {
				result += arg
			}
			
			override fun putChar(arg: Char) {
				result += arg
			}
		})
		machine.run()
		return result
	}
	
	@Test
	fun addTest() {
		assertEquals("3", executeProgram(Program(listOf<Instruction>(
			Instruction.push(1),
			Instruction.push(2),
			Instruction.add(),
			Instruction.writei(),
			Instruction.exit()
		))))
	}
	
	@Test
	fun printSequenceTest() {
		assertEquals("1\n2\n3\n4\n5\n", executeProgram(Program(listOf<Instruction>(
			Instruction.push(1),
			Instruction.label(0),
			Instruction.dup(),
			Instruction.writei(),
			Instruction.push('\n'.code),
			Instruction.writech(),
			Instruction.push(1),
			Instruction.add(),
			Instruction.dup(),
			Instruction.push(6),
			Instruction.sub(),
			Instruction.jmpNeg(0),
			Instruction.exit()
		))))
	}
}