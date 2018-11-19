// This file is part of www.nand2tetris.org
// and the book "The Elements of Computing Systems"
// by Nisan and Schocken, MIT Press.
// File name: projects/04/Fill.asm

// Runs an infinite loop that listens to the keyboard input.
// When a key is pressed (any key), the program blackens the screen,
// i.e. writes "black" in every pixel;
// the screen should remain fully black as long as the key is pressed. 
// When no key is pressed, the program clears the screen, i.e. writes
// "white" in every pixel;
// the screen should remain fully clear as long as no key is pressed.


	@24575
	D=A
	@SCREEN
	D=D-A
	@n
	M=D //make n = size of screen
	
	
(LOOP)
	@i
	M=0 // i = 0
	@SCREEN
	D=A
	@addr
	M=D // reset RAM[addr] = SCREEN 
	@KBD
	D=M // D = value of keyboard pressed
	@PAINTBLACK
	D;JGT // if D > 0, goto PAINTBLACK
	@PAINTWHITE
	0;JMP // else goto PAINTWHITE
	
(PAINTBLACK)
	@i
	D=M
	@n
	D=D-M
	@STOP
	D;JGT // if i > n, goto STOP
	
	@addr
	A=M
	M=-1 // RAM[addr] = 111111111111111
	
	@i
	M=M+1 // i = i + 1
	@addr
	M=M+1 // RAM[addr] = RAM[addr] + 1
	@PAINTBLACK
	0;JMP
	
(PAINTWHITE)
	@i
	D=M
	@n
	D=D-M
	@STOP
	D;JGT // if i > n, goto STOP
	
	@addr
	A=M
	M=0 // RAM[addr] = 000000000000000
	
	@i
	M=M+1 // i = i + 1
	@addr
	M=M+1 // RAM[addr] = RAM[addr] + 1
	@PAINTWHITE
	0;JMP
	
(STOP)
	@LOOP
	0;JMP
	