	.file	1 "lcs.c"
	.section .mdebug.abi32
	.previous
	.gnu_attribute 4, 1
	.abicalls
	.section	.text.startup,"ax",@progbits
	.align	2
	.globl	main
	.set	nomips16
	.ent	main
	.type	main, @function
main:
	.frame	$sp,0,$31		# vars= 0, regs= 0/0, args= 0, gp= 0
	.mask	0x00000000,0
	.fmask	0x00000000,0
	.set	noreorder
	.cpload	$25
	.set	reorder
	move	$2,$0
	j	$31
	.end	main
	.size	main, .-main
	.text
	.align	2
	.globl	sfe_main
	.set	nomips16
	.ent	sfe_main
	.type	sfe_main, @function
sfe_main:
	.frame	$sp,72,$31		# vars= 64, regs= 0/0, args= 0, gp= 8
	.mask	0x00000000,0
	.fmask	0x00000000,0
	.set	noreorder
	.cpload	$25
	.set	reorder
	addiu	$sp,$sp,-72
	.cprestore	0
	lb	$2,0($5)
	lb	$7,0($4)
	lb	$6,1($5)
	xor	$8,$7,$2
	sltu	$9,$8,1
	sw	$0,24($sp)
	sw	$0,40($sp)
	sw	$0,56($sp)
	sw	$0,16($sp)
	sw	$0,20($sp)
	lb	$3,2($5)
	sw	$9,28($sp)
	beq	$7,$6,$L33
	nop				# Y 1
$L8:
	sw	$9,32($sp)
	beq	$7,$3,$L34
	lw	$5,20($sp)
	move	$11,$9
	slt	$10,$9,$5
	bne	$10,$0,$L35
$L11:
	lb	$13,1($4)
	sw	$11,36($sp)
	beq	$13,$2,$L36
$L13:
	lw	$14,28($sp)
	lw	$10,40($sp)
	#nop
	slt	$15,$10,$14
	bne	$15,$0,$L37
$L14:
	sw	$10,44($sp)
	beq	$13,$6,$L38
$L16:
	lw	$25,32($sp)
	#nop
	slt	$8,$10,$25
	bne	$8,$0,$L39
$L17:
	sw	$10,48($sp)
	beq	$13,$3,$L40
$L19:
	lw	$9,36($sp)
	#nop
	slt	$5,$10,$9
	bne	$5,$0,$L41
$L20:
	lb	$4,2($4)
	sw	$10,52($sp)
	beq	$4,$2,$L22
$L47:
	lw	$12,56($sp)
	lw	$2,44($sp)
	#nop
	slt	$13,$2,$12
	bne	$13,$0,$L42
$L24:
	beq	$4,$6,$L25
$L46:
	lw	$14,48($sp)
	#nop
	slt	$6,$2,$14
	bne	$6,$0,$L43
$L27:
	beq	$4,$3,$L28
$L45:
	lw	$3,52($sp)
	#nop
	slt	$24,$2,$3
	bne	$24,$0,$L44
	addiu	$sp,$sp,72
	j	$31
$L44:
	move	$2,$3
	addiu	$sp,$sp,72
	j	$31
$L43:
	move	$2,$14
	bne	$4,$3,$L45
$L28:
	lw	$25,48($sp)
	addiu	$sp,$sp,72
	addiu	$2,$25,1
	j	$31
$L42:
	move	$2,$12
	bne	$4,$6,$L46
$L25:
	nop				# Y 7
	lw	$15,44($sp)
	#nop
	addiu	$2,$15,1
	b	$L27
$L41:
	lb	$4,2($4)
	move	$10,$9
	sw	$10,52($sp)
	bne	$4,$2,$L47
$L22:
	nop				# Y 6
	lw	$2,40($sp)
	#nop
	addiu	$2,$2,1
	b	$L24
$L39:
	move	$10,$25
	sw	$10,48($sp)
	bne	$13,$3,$L19
$L40:
	nop				# Y 5
	lw	$11,32($sp)
	#nop
	addiu	$10,$11,1
	b	$L20
$L37:
	move	$10,$14
	sw	$10,44($sp)
	bne	$13,$6,$L16
$L38:
	nop				# Y 4
	lw	$7,28($sp)
	#nop
	addiu	$10,$7,1
	b	$L17
$L35:
	lb	$13,1($4)
	move	$11,$5
	sw	$11,36($sp)
	bne	$13,$2,$L13
	nop				# Y 8
$L36:
	nop				# Y 3
	lw	$24,24($sp)
	#nop
	addiu	$10,$24,1
	b	$L14
$L34:
	nop				# Y 2
	lw	$12,16($sp)
	#nop
	addiu	$11,$12,1
	b	$L11
$L33:
	li	$9,1			# 0x1
	b	$L8
	.end	sfe_main
	.size	sfe_main, .-sfe_main
	.ident	"GCC: (GNU) 4.7.3"
