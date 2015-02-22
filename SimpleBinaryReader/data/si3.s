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
	.frame	$sp,0,$31		# vars= 0, regs= 0/0, args= 0, gp= 0
	.mask	0x00000000,0
	.fmask	0x00000000,0
	.set	noreorder
	.cpload	$25
	.set	reorder
	move	$2,$0
	blez	$6,$L7
	blez	$7,$L2
	move	$10,$0
	move	$3,$0
	b	$L6
$L13:
	addiu	$3,$3,1
$L4:
	slt	$25,$3,$6
	slt	$9,$10,$7
	beq	$25,$0,$L2
$L14:
	beq	$9,$0,$L12
$L6:
	sll	$9,$3,2
	sll	$8,$10,2
	addu	$11,$4,$9
	addu	$12,$5,$8
	lw	$13,0($11)
	lw	$14,0($12)
	#nop
	slt	$15,$13,$14
	slt	$24,$14,$13
	bne	$15,$0,$L13
	beq	$24,$0,$L5
	addiu	$10,$10,1
	slt	$25,$3,$6
	slt	$9,$10,$7
	bne	$25,$0,$L14
$L2:
	j	$31
$L5:
	addiu	$3,$3,1
	addiu	$2,$2,1
	b	$L4
$L12:
	j	$31
$L7:
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
	.section	.text.startup,"ax",@progbits
	.align	2
	.globl	main
	.set	nomips16
	.ent	main
	.type	main, @function
main:
	.frame	$sp,192,$31		# vars= 160, regs= 1/0, args= 16, gp= 8
	.mask	0x80000000,-4
	.fmask	0x00000000,0
	.set	noreorder
	.cpload	$25
	.set	reorder
	addiu	$sp,$sp,-192
	sw	$31,188($sp)
	lw	$2,$LC0
	lw	$3,$LC0+4
	lw	$4,$LC0+8
	lw	$5,$LC0+12
	lw	$6,$LC0+16
	lw	$9,$LC0+28
	lw	$7,$LC0+20
	lw	$8,$LC0+24
	lw	$10,$LC0+32
	lw	$11,$LC0+36
	lw	$12,$LC0+40
	lw	$13,$LC0+44
	lw	$14,$LC0+48
	lw	$15,$LC0+52
	lw	$24,$LC0+56
	lw	$25,$LC0+60
	.cprestore	16
	sw	$2,104($sp)
	sw	$3,108($sp)
	sw	$4,112($sp)
	sw	$5,116($sp)
	sw	$6,120($sp)
	sw	$9,132($sp)
	sw	$7,124($sp)
	sw	$8,128($sp)
	sw	$10,136($sp)
	sw	$11,140($sp)
	sw	$12,144($sp)
	sw	$13,148($sp)
	sw	$14,152($sp)
	sw	$15,156($sp)
	sw	$24,160($sp)
	sw	$25,164($sp)
	lw	$31,$LC0+64
	lw	$4,$LC0+68
	lw	$5,$LC0+72
	lw	$6,$LC0+76
	la	$2,$LC1
	sw	$31,168($sp)
	addiu	$3,$sp,24
	sw	$4,172($sp)
	sw	$5,176($sp)
	sw	$6,180($sp)
	la	$9,$LC1+80
$L17:
	lw	$8,0($2)
	lw	$7,4($2)
	lw	$10,8($2)
	lw	$11,12($2)
	addiu	$2,$2,16
	sw	$8,0($3)
	sw	$7,4($3)
	sw	$10,8($3)
	sw	$11,12($3)
	addiu	$3,$3,16
	bne	$2,$9,$L17
	addiu	$4,$sp,104
	addiu	$5,$sp,24
	li	$6,20			# 0x14
	li	$7,20			# 0x14
	jal	sfe_main
	lw	$31,188($sp)
	move	$2,$0
	addiu	$sp,$sp,192
	j	$31
	.end	main
	.size	main, .-main
	.ident	"GCC: (GNU) 4.7.3"
