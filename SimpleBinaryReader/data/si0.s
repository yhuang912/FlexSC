	.file	1 "si.c"
	.section .mdebug.abi32
	.previous
	.gnu_attribute 4, 1
	.abicalls
	.text
	.align	2
	.globl	sfe_main
	.set	nomips16
	.ent	sfe_main
	.type	sfe_main, @function
sfe_main:
	.frame	$fp,32,$31		# vars= 16, regs= 1/0, args= 0, gp= 8
	.mask	0x40000000,-4
	.fmask	0x00000000,0
	.set	noreorder
	.cpload	$25
	.set	reorder
	addiu	$sp,$sp,-32
	sw	$fp,28($sp)
	move	$fp,$sp
	.cprestore	0
	sw	$4,32($fp)
	sw	$5,36($fp)
	sw	$6,40($fp)
	sw	$7,44($fp)
	sw	$0,8($fp)
	sw	$0,12($fp)
	sw	$0,16($fp)
	b	$L2
$L6:
	lw	$2,8($fp)
	#nop
	sll	$2,$2,2
	lw	$3,32($fp)
	#nop
	addu	$2,$3,$2
	lw	$3,0($2)
	lw	$2,12($fp)
	#nop
	sll	$2,$2,2
	lw	$4,36($fp)
	#nop
	addu	$2,$4,$2
	lw	$2,0($2)
	#nop
	slt	$2,$3,$2
	beq	$2,$0,$L3
	lw	$2,8($fp)
	#nop
	addiu	$2,$2,1
	sw	$2,8($fp)
	b	$L2
$L3:
	lw	$2,12($fp)
	#nop
	sll	$2,$2,2
	lw	$3,36($fp)
	#nop
	addu	$2,$3,$2
	lw	$3,0($2)
	lw	$2,8($fp)
	#nop
	sll	$2,$2,2
	lw	$4,32($fp)
	#nop
	addu	$2,$4,$2
	lw	$2,0($2)
	#nop
	slt	$2,$3,$2
	beq	$2,$0,$L4
	lw	$2,12($fp)
	#nop
	addiu	$2,$2,1
	sw	$2,12($fp)
	b	$L2
$L4:
	lw	$2,8($fp)
	#nop
	addiu	$2,$2,1
	sw	$2,8($fp)
	lw	$2,16($fp)
	#nop
	addiu	$2,$2,1
	sw	$2,16($fp)
$L2:
	lw	$3,8($fp)
	lw	$2,40($fp)
	#nop
	slt	$2,$3,$2
	beq	$2,$0,$L5
	lw	$3,12($fp)
	lw	$2,44($fp)
	#nop
	slt	$2,$3,$2
	bne	$2,$0,$L6
$L5:
	lw	$2,16($fp)
	move	$sp,$fp
	lw	$fp,28($sp)
	addiu	$sp,$sp,32
	j	$31
	.end	sfe_main
	.size	sfe_main, .-sfe_main
	.rdata
	.align	2
$LC0:
	.word	4
	.word	33
	.word	54
	.word	57
	.word	65
	.word	70
	.word	75
	.word	83
	.word	111
	.word	113
	.word	118
	.word	124
	.word	129
	.word	132
	.word	144
	.word	155
	.word	170
	.word	175
	.word	187
	.word	189
	.align	2
$LC1:
	.word	5
	.word	19
	.word	21
	.word	38
	.word	46
	.word	60
	.word	64
	.word	65
	.word	72
	.word	73
	.word	77
	.word	78
	.word	80
	.word	120
	.word	144
	.word	148
	.word	156
	.word	175
	.word	190
	.word	196
	.text
	.align	2
	.globl	main
	.set	nomips16
	.ent	main
	.type	main, @function
main:
	.frame	$fp,208,$31		# vars= 176, regs= 2/0, args= 16, gp= 8
	.mask	0xc0000000,-4
	.fmask	0x00000000,0
	.set	noreorder
	.cpload	$25
	.set	reorder
	addiu	$sp,$sp,-208
	sw	$31,204($sp)
	sw	$fp,200($sp)
	move	$fp,$sp
	.cprestore	16
	li	$2,20			# 0x14
	sw	$2,24($fp)
	lw	$2,24($fp)
	#nop
	sw	$2,28($fp)
	addiu	$4,$fp,36
	la	$3,$LC0
	li	$2,80			# 0x50
	move	$5,$3
	move	$6,$2
	jal	memcpy
	addiu	$4,$fp,116
	la	$3,$LC1
	li	$2,80			# 0x50
	move	$5,$3
	move	$6,$2
	jal	memcpy
	addiu	$3,$fp,36
	addiu	$2,$fp,116
	move	$4,$3
	move	$5,$2
	lw	$6,24($fp)
	lw	$7,28($fp)
	jal	sfe_main
	sw	$2,32($fp)
	move	$2,$0
	move	$sp,$fp
	lw	$31,204($sp)
	lw	$fp,200($sp)
	addiu	$sp,$sp,208
	j	$31
	.end	main
	.size	main, .-main
	.ident	"GCC: (GNU) 4.7.3"
