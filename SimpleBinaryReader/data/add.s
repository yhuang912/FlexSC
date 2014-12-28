	.abicalls
	.option	pic0
	.section .mdebug.abi32
	.previous
	.file	"add.c"
	.text
	.globl	sfe_main
	.align	2
	.type	sfe_main,@function
	.set	nomips16
	.ent	sfe_main
sfe_main:
	.frame	$fp,24,$ra
	.mask 	0x40000000,-4
	.fmask	0x00000000,0
	.set	noreorder
	.set	nomacro
	.set	noat
	addiu	$sp, $sp, -24
	sw	$fp, 20($sp)
	move	 $fp, $sp
	sw	$4, 16($fp)
	sw	$5, 12($fp)
	lw	$1, 16($fp)
	addu	$2, $1, $5
	sw	$4, 8($fp)
	sw	$5, 4($fp)
	move	 $sp, $fp
	lw	$fp, 20($sp)
	addiu	$sp, $sp, 24
	jr	$ra
	nop
	.set	at
	.set	macro
	.set	reorder
	.end	sfe_main
$tmp3:
	.size	sfe_main, ($tmp3)-sfe_main

	.globl	main
	.align	2
	.type	main,@function
	.set	nomips16
	.ent	main
main:
	.frame	$fp,56,$ra
	.mask 	0xc0000000,-4
	.fmask	0x00000000,0
	.set	noreorder
	.set	nomacro
	.set	noat
	addiu	$sp, $sp, -56
	sw	$ra, 52($sp)
	sw	$fp, 48($sp)
	move	 $fp, $sp
	sw	$zero, 44($fp)
	sw	$4, 40($fp)
	sw	$5, 36($fp)
	addiu	$1, $zero, 1
	addiu	$2, $zero, 2
	sw	$4, 32($fp)
	move	 $4, $1
	sw	$5, 28($fp)
	move	 $5, $2
	jal	sfe_main
	nop
	lw	$1, 28($fp)
	lw	$4, 32($fp)
	addiu	$5, $zero, 0
	sw	$2, 24($fp)
	move	 $2, $5
	sw	$4, 20($fp)
	sw	$1, 16($fp)
	move	 $sp, $fp
	lw	$fp, 48($sp)
	lw	$ra, 52($sp)
	addiu	$sp, $sp, 56
	jr	$ra
	nop
	.set	at
	.set	macro
	.set	reorder
	.end	main
$tmp7:
	.size	main, ($tmp7)-main


	.ident	"clang version 3.4 (tags/RELEASE_34/final)"
