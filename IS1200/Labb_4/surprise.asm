# only allowed register $0, $at, $v0, $v1, $a0, $a1, $a2 and $a3
# !0 = 1	!1 = 1		!3 = 6		!4 = 24		!8 = 40320


addi $a0, $0, 8				# n
addi $a0, $a0, 1
addi $v0, $0, 1					# res
addi $a1, $0, 1					# i = 1

beq $a0, $0, specialcase		# n == 0
add  $0, $0, $0					# NOP

loop:
	beq $a1, $a0, done			# i == n
	add  $0, $0, $0				# NOP
	
	mul $v0, $v0, $a1			# multi v0(n) med a1(i)
	addi $a1, $a1, 1			# i++
	beq $0, $0, loop			# hoppar till loop och multi med nästa tal 
	add  $0,$0,$0	  			# NOP

specialcase:
	addi $v0, $0, 1

done:
	add  $0,$0,$0	  			# NOP
	#beq $0, $0, done


#console:				# För console utskrift, kommentera bort rad 20
	addi $t0, $v0, 0  		# spar v0
	li  $v0, 1          		# service 1 is print integer
	add $a0, $t0, $0		# flyttar t0 (v0) värdet till a0
	syscall				# skriver till console