  # labwork.S
  # Written 2015-2017 by F Lundevall
  # Skeleton file for IS1200/IS1500 lab 1.
  # The macros PUSH and POP are in the public domain.
  # Please add your own code at the end of the file.

  #
  # Please keep the two macros PUSH and POP unchanged
  #
.macro	PUSH reg
	addi	$sp,$sp,-4
	sw	\reg,0($sp)
.endm

.macro	POP reg
	lw	\reg,0($sp)
	addi	$sp,$sp,4
.endm
  #
  # Please add your own code below this line
  #
  
.text
#.data 
.global delay
.global time2string


time2string:
	time2string:		
	PUSH $ra					# Spar return value p? stacken
	PUSH $a1					# Spar a1 p? stacken
	PUSH $a0					# Spar time p? stacken
	
								# F?rsta sekunden(fr?n h?ger) 
	andi $a0, $a1, 0xf			# H?mtar ut fyra LSB 
	
	
	jal hexasc					# G?r om enligt ASCII-tabell 
	nop	
	POP $a0						# H?mtar ordinarie argument fr?n stacken
	sb $v0, 4($a0)				# Sparar ner returv?rdet fr?n hexasc
	PUSH $a0					# Spar ordinarire argument p? stacken
	
								# Andra sekunden (fr?n h?ger)
	andi $a0, $a1, 0xf0			# H?mtar ut 8 LSB 

	srl $a0, $a0, 4				# Shiftbitar 4 till v?nster, f?r att f? sekunden p? LSB
	jal hexasc					# G?r om enligt ASCII-tabell
	nop
	POP $a0						# H?mtar ordinarie argument0 fr?n stacken
	sb $v0, 3($a0)
	PUSH $a0					# Sparar ordinarie argument0 p? stacken
	
								# F?rsta minuten(fr?n h?ger)
	andi $a0, $a1, 0xf00		# H?mtar ut 12 LSB
	srl $a0, $a0, 8				# Shiftbitar 8 till v?nster f?r att f? minuten p? LSB
	jal hexasc					# G?r om enligt ACSII-tabell
	nop	
	POP $a0						# H?mtar ordinarie argument0 fr?n stacken
	sb $v0, 1($a0)				# Sparar ner returv?rdet f?r hexasc
	PUSH $a0
								# Andra minuten(fr?n h?ger)
	andi $a0, $a1, 0xf000		# H?mtar ut 16LSB 
	srl $a0, $a0, 12			# Shiftbitar 12 till v?nster f?r att f? minuten p? LSB
	jal hexasc					# G?r om enligt ASCII-tabell
	nop
	POP $a0						# H?mtar ordinarie argument0 fr?n stacken
	sb $v0, 0($a0)				# Sparar ner returv?rdet fr?n hexasc
	PUSH $a0					# sparar ordinarie argument0 p? stackenn
	
	
		
	addi $s0, $0, 0x3a			# L?gger till ASCII-kod f?r : 
	sb $s0, 2($a0)			 
	
	addi $s0, $0, 0x00			# L?gger till 0:or p? 8 LSB
	sb $s0,5($a0)			

	
	POP $a0						# H?mtar ordinarie argument0 fr?n stacken					
	POP $a1						# H?mtar ordinarie argument1 fr?n stacken
	andi $t0, $a1, 0xf			# Kollar om sekund = x9
	beq $t0, 9, suprise			# Hopppar till suprise
	

	POP $ra						# H?mtar return value fr?n stacken
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
	
	addi $t0, $0, 0x0			# L?gger till 0:or p? 8 LSB
	sb $t0,8($a0)			
	
	POP $ra
	jr $ra	

hexasc: 
	andi $a0, $a0, 0xf			# Spar endast 4 LSB
	ble	$a0, 9, hexToASCII		#Kollar om talet ?r < 9, is?fall k?rs numToASCII
	nop
	
								#K?rs om talet ?r > 9 
	addi	$v0, $a0, 0x37		#Plats 0x37 + tal ( 10 <= tal <= 15 ) ger r?tt ASCI plats. Tal mellan 10 - 15 ger A - F
	andi	$v0, $v0, 0x7F		#Tar bort alla bitar utom 7 LSB	
	jr	$ra						#Hoppar tillbaka till huvudprogramet
	nop
	
hexToASCII:

	addi	$v0, $a0, 0x30		#Plats 0x30 + tal, (0 <= tal <= 9 ) ger r?tt ACSI plats. Tal mellan 0-9 ger 0 - 9. 	
	andi	$v0, $v0, 0x7F	
	jr	$ra						#Hoppar tillbaka till huvudprogrammet 
	nop

delay: 	
								# i = $t0
								# ms = $a0
								
	addi $t0, $0, 0				# for-loopen i = 0
	addi $a0, $a0, -1			# ms = ms - 1
	addi $t1, $0, 5800			# for-loopen "slut statement"			anropas standard med 2, 2500 = 1000ms	7500 = 3000ms
			
loop: 							#for loopen
	addi $t0, $t0, 1			# ?kar t0 med 1 				i + 1
	ble $t0, $t1, loop			# $t0 < $t1, forts?tt loopa for-loopen 
	nop
	
	bne $a0, $0, delay			#while-loopen, s? l?nge ms > 0
	nop
	
	jr $ra
	nop

