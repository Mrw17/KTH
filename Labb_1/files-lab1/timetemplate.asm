  # timetemplate.asm
  # Written 2015 by F Lundevall
  # Copyright abandonded - this file is in the public domain.

.macro	PUSH (%reg)
	addi	$sp,$sp,-4
	sw	%reg,0($sp)
.end_macro

.macro	POP (%reg)
	lw	%reg,0($sp)
	addi	$sp,$sp,4
.end_macro

	.data
	.align 2
mytime:	.word 0x5957
timstr:	.ascii "text more text lots of text\0"
	.text
main:
	# print timstr
	la	$a0,timstr
	li	$v0,4
	syscall
	nop
	# wait a little
	li	$a0,200
	jal	delay 
	nop
	# call tick
	la	$a0,mytime
	jal	tick
	nop
	# call your function time2string
	la	$a0,timstr
	la	$t0,mytime
	lw	$a1,0($t0)
	jal	time2string
	nop
	# print a newline
	li	$a0,10
	li	$v0,11
	syscall
	nop
	# go back and do it all again
	j	main
	nop
# tick: update time pointed to by $a0
tick:	lw	$t0,0($a0)	# get time
	addiu	$t0,$t0,1	# increase
	andi	$t1,$t0,0xf	# check lowest digit
	sltiu	$t2,$t1,0xa	# if digit < a, okay
	bnez	$t2,tiend
	nop
	addiu	$t0,$t0,0x6	# adjust lowest digit
	andi	$t1,$t0,0xf0	# check next digit
	sltiu	$t2,$t1,0x60	# if digit < 6, okay
	bnez	$t2,tiend
	nop
	addiu	$t0,$t0,0xa0	# adjust digit
	andi	$t1,$t0,0xf00	# check minute digit
	sltiu	$t2,$t1,0xa00	# if digit < a, okay
	bnez	$t2,tiend
	nop
	addiu	$t0,$t0,0x600	# adjust digit
	andi	$t1,$t0,0xf000	# check last digit
	sltiu	$t2,$t1,0x6000	# if digit < 6, okay
	bnez	$t2,tiend
	nop
	addiu	$t0,$t0,0xa000	# adjust last digit
tiend:	sw	$t0,0($a0)	# save updated result
	jr	$ra		# return
	nop

  # you can write your code for subroutine "hexasc" below this line
  #

time2string:
	PUSH $ra			# Spar return value p� stacken
	PUSH $a1			# Spar a1 p� stacken
	PUSH $a0			# Spar time p� stacken
	
					# F�rsta sekunden(fr�n h�ger) 
	andi $a0, $a1, 0xf		# H�mtar ut fyra LSB 
	
	
	jal hexasc			# G�r om enligt ASCII-tabell 
	nop	
	POP $a0				# H�mtar ordinarie argument fr�n stacken
	sb $v0, 4($a0)			# Sparar ner returv�rdet fr�n hexasc
	PUSH $a0			# Spar ordinarire argument p� stacken
	
					# Andra sekunden (fr�n h�ger)
	andi $a0, $a1, 0xf0		# H�mtar ut 8 LSB 

	srl $a0, $a0, 4			# Shiftbitar 4 till v�nster, f�r att f� sekunden p� LSB
	jal hexasc			# G�r om enligt ASCII-tabell
	nop
	POP $a0				# H�mtar ordinarie argument0 fr�n stacken
	sb $v0, 3($a0)
	PUSH $a0			# Sparar ordinarie argument0 p� stacken
	
					# F�rsta minuten(fr�n h�ger)
	andi $a0, $a1, 0xf00		# H�mtar ut 12 LSB
	srl $a0, $a0, 8			# Shiftbitar 8 till v�nster f�r att f� minuten p� LSB
	jal hexasc			# G�r om enligt ACSII-tabell
	nop	
	POP $a0				# H�mtar ordinarie argument0 fr�n stacken
	sb $v0, 1($a0)			# Sparar ner returv�rdet f�r hexasc
	PUSH $a0
					# Andra minuten(fr�n h�ger)
	andi $a0, $a1, 0xf000		# H�mtar ut 16LSB 
	srl $a0, $a0, 12		# Shiftbitar 12 till v�nster f�r att f� minuten p� LSB
	jal hexasc			# G�r om enligt ASCII-tabell
	nop
	POP $a0				# H�mtar ordinarie argument0 fr�n stacken
	sb $v0, 0($a0)			# Sparar ner returv�rdet fr�n hexasc
	PUSH $a0			# sparar ordinarie argument0 p� stackenn
	
	
		
	addi $s0, $0, 0x3a		# L�gger till ASCII-kod f�r : 
	sb $s0, 2($a0)			 
	
	addi $s0, $0, 0x00		# L�gger till 0:or p� 8 LSB
	sb $s0,5($a0)			

	
	POP $a0				# H�mtar ordinarie argument0 fr�n stacken					
	POP $a1				# H�mtar ordinarie argument1 fr�n stacken
	
	#SUPRISE 
	andi $t0, $a1, 0xf		# Kollar om sekund = x9
	beq $t0, 9, suprise		# Hopppar till suprise
	

	POP $ra				# H�mtar return value fr�n stacken
	jr $ra
	nop
	
suprise:

	#N
	addi $t0, $0, 0x4e
	sb $t0, 4($a0)		
	
	#I
	addi $t0, $0, 0x49
	sb $t0, 5($a0)		

	#N
	addi $t0, $0, 0x4e
	sb $t0, 6($a0)		
	
	#E
	addi $t0, $0, 0x45
	sb $t0, 7($a0)
	
	addi $t0, $0, 0x0		# L�gger till 0:or p� 8 LSB
	sb $t0,8($a0)			
	
	POP $ra
	jr $ra	

hexasc: 
	andi $a0, $a0, 0xf		# Spar endast 4 LSB
	ble	$a0, 9, hexToASCII	#Kollar om talet �r < 9, is�fall k�rs numToASCII
	nop
	
					#K�rs om talet �r > 9 
	addi	$v0, $a0, 0x37		#Plats 0x37 + tal ( 10 <= tal <= 15 ) ger r�tt ASCI plats. Tal mellan 10 - 15 ger A - F
	andi	$v0, $v0, 0x7F		#Tar bort alla bitar utom 7 LSB	
	jr	$ra			#Hoppar tillbaka till huvudprogramet
	nop
	
hexToASCII:

	addi	$v0, $a0, 0x30		#Plats 0x30 + tal, (0 <= tal <= 9 ) ger r�tt ACSI plats. Tal mellan 0-9 ger 0 - 9. 	
	andi	$v0, $v0, 0x7F	
	jr	$ra			#Hoppar tillbaka till huvudprogrammet 
	nop

delay: 
					# L�gger till en delay p� 1000ms 
	#li $v0, 32
	#li $a0, 1000
	#syscall
	#jr $ra
	
	
					# i = $t0
					# ms = $a0
	
	addi $t0, $0, 0			# for-loopen i = 0
	addi $a0, $a0, -1		# ms = ms - 1
	addi $t1, $0, 2000		# for-loopen "slut statement"			anropas standard med 2, 2500 = 1000ms	7500 = 3000ms
			
loop: 											#for loopen
	addi $t0, $t0, 1		# �kar t0 med 1 				i + 1
	ble $t0, $t1, loop		# $t0 < $t1, forts�tt loopa 			for-loopen 
	nop
	
	bne $a0, $0, delay									#while-loopen, s� l�nge ms > 0
	nop
	
	jr $ra
	nop
