	.file	1 "lcs_correct.c"
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
	.frame	$sp,408,$31		# vars= 400, regs= 0/0, args= 0, gp= 8
	.mask	0x00000000,0
	.fmask	0x00000000,0
	.set	noreorder
	.cpload	$25
	.set	reorder
	addiu	$sp,$sp,-408
	.cprestore	0
	addiu	$2,$sp,8
	move	$8,$2
	addiu	$6,$sp,408
	move	$3,$2
$L2:
	sw	$0,0($3)
	addiu	$3,$3,40
	bne	$3,$6,$L2
	addiu	$3,$8,40
$L3:
	sw	$0,0($2)
	addiu	$2,$2,4
	bne	$2,$3,$L3
	addiu	$8,$8,80
	li	$10,1			# 0x1
	move	$2,$0
	li	$12,10			# 0xa
	b	$L4
$L8:
	lb	$7,0($6)
	#nop
	bne	$7,$9,$L5
	lw	$7,-44($3)
	#nop
	addiu	$7,$7,1
	sw	$7,0($3)
	slt	$11,$2,$7
	beq	$11,$0,$L7
	move	$2,$7
	b	$L7
$L5:
	sw	$0,0($3)
$L7:
	addiu	$6,$6,1
	addiu	$3,$3,4
	bne	$3,$8,$L8
	addiu	$10,$10,1
	addiu	$8,$8,40
	beq	$10,$12,$L9
$L4:
	addu	$3,$4,$10
	lb	$9,-1($3)
	move	$6,$5
	sll	$7,$10,3
	sll	$3,$10,5
	addu	$3,$7,$3
	addiu	$3,$3,4
	addiu	$7,$sp,8
	addu	$3,$7,$3
	b	$L8
$L9:
	addiu	$sp,$sp,408
	j	$31
	.end	sfe_main
	.size	sfe_main, .-sfe_main
	.section	.rodata.str1.4,"aMS",@progbits,1
	.align	2
$LC2:
	.ascii	"%d\012\000"
	.align	2
$LC0:
	.ascii	"aaaaaaaaa\000"
	.align	2
$LC1:
	.ascii	"bbaaabaab\000"
	.text
	.align	2
	.globl	main
	.set	nomips16
	.ent	main
	.type	main, @function
main:
	.frame	$sp,56,$31		# vars= 24, regs= 1/0, args= 16, gp= 8
	.mask	0x80000000,-4
	.fmask	0x00000000,0
	.set	noreorder
	.cpload	$25
	.set	reorder
	addiu	$sp,$sp,-56
	sw	$31,52($sp)
	.cprestore	16
	lw	$3,$LC0
	lw	$2,$LC0+4
	sw	$3,24($sp)
	sw	$2,28($sp)
	lhu	$2,$LC0+8
	#nop
	sh	$2,32($sp)
	lw	$3,$LC1
	lw	$2,$LC1+4
	sw	$3,36($sp)
	sw	$2,40($sp)
	lhu	$2,$LC1+8
	#nop
	sh	$2,44($sp)
	addiu	$4,$sp,24
	addiu	$5,$sp,36
	jal	sfe_main
	la	$4,$LC2
	move	$5,$2
	jal	printf
	move	$2,$0
	lw	$31,52($sp)
	addiu	$sp,$sp,56
	j	$31
	.end	main
	.size	main, .-main
	.ident	"GCC: (GNU) 4.7.3"
