# Whitespace language library
![GitHub License](https://img.shields.io/github/license/galloj/whitespace)

This is clean-room implementation of Whitespace interpreter in Kotlin. The main purpose of the library is the execution of Whitespace code. It also offers parsing Whitespace language, converting parsed representation back into Whitespace, converting the representation into human-readable form, and also converting the human-readable form back to representation.

## Executing Whitespace code

```kotlin
val code = "..."; // yours code goes here
val program = Program.fromCode(code);
val machine = Machine(program, object: IOInterface {
    override fun getInt(): Int {
		return Scanner(System.`in`).nextInt()
    }
    
    override fun getChar(): Char {
		return System.`in`.read().toChar()
    }
    
    override fun putInt(arg: Int) {
        print(arg)
    }
    
    override fun putChar(arg: Char) {
        print(arg)
    }
})
machine.run()
```

## Viewing human-readable representation

```kotlin
val code = "..."; // yours code goes here
val program = Program.fromCode(code);
print(program.getRepresentation())
```