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
	.frame	$sp,408,$31		# vars= 400, regs= 0/0, args= 0, gp= 8
	.mask	0x00000000,0
	.fmask	0x00000000,0
	.set	noreorder
	.cpload	$25
	.set	reorder
	addiu	$sp,$sp,-408
	.cprestore	0
	lb	$12,0($5)
	lb	$3,0($4)
	lb	$11,1($5)
	xor	$13,$3,$12
	sltu	$13,$13,1
	lb	$10,2($5)
	lb	$9,3($5)
	lb	$8,4($5)
	lb	$7,5($5)
	lb	$6,6($5)
	lb	$2,7($5)
	sw	$0,48($sp)
	sw	$0,88($sp)
	sw	$0,128($sp)
	sw	$0,168($sp)
	sw	$0,208($sp)
	sw	$0,248($sp)
	sw	$0,288($sp)
	sw	$0,328($sp)
	sw	$0,368($sp)
	sw	$0,16($sp)
	sw	$0,20($sp)
	sw	$0,24($sp)
	sw	$0,28($sp)
	sw	$0,32($sp)
	sw	$0,36($sp)
	sw	$0,40($sp)
	sw	$0,44($sp)
	lb	$5,8($5)
	sw	$13,52($sp)
	beq	$3,$11,$L249
	nop				# Y 1
$L8:
	sw	$13,56($sp)
	beq	$3,$10,$L250
	lw	$14,20($sp)
	#nop
	slt	$15,$13,$14
	bne	$15,$0,$L251
$L11:
	sw	$13,60($sp)
	beq	$3,$9,$L252
$L13:
	lw	$25,24($sp)
	#nop
	slt	$14,$13,$25
	bne	$14,$0,$L253
$L14:
	sw	$13,64($sp)
	beq	$3,$8,$L254
$L16:
	lw	$24,28($sp)
	#nop
	slt	$15,$13,$24
	bne	$15,$0,$L255
$L17:
	sw	$13,68($sp)
	beq	$7,$3,$L256
$L19:
	lw	$14,32($sp)
	#nop
	slt	$24,$13,$14
	bne	$24,$0,$L257
$L20:
	sw	$13,72($sp)
	beq	$3,$6,$L258
$L22:
	lw	$25,36($sp)
	#nop
	slt	$15,$13,$25
	bne	$15,$0,$L259
$L23:
	sw	$13,76($sp)
	beq	$3,$2,$L260
$L25:
	lw	$24,40($sp)
	#nop
	slt	$25,$13,$24
	bne	$25,$0,$L261
$L26:
	sw	$13,80($sp)
	beq	$5,$3,$L262
$L28:
	lw	$3,44($sp)
	#nop
	slt	$15,$13,$3
	bne	$15,$0,$L263
$L29:
	lb	$24,1($4)
	sw	$13,84($sp)
	beq	$24,$12,$L264
$L31:
	lw	$25,88($sp)
	lw	$13,52($sp)
	#nop
	slt	$3,$13,$25
	bne	$3,$0,$L265
$L32:
	sw	$13,92($sp)
	beq	$24,$11,$L266
$L34:
	lw	$14,56($sp)
	#nop
	slt	$25,$13,$14
	bne	$25,$0,$L267
$L35:
	sw	$13,96($sp)
	beq	$24,$10,$L268
$L37:
	lw	$3,60($sp)
	#nop
	slt	$15,$13,$3
	bne	$15,$0,$L269
$L38:
	sw	$13,100($sp)
	beq	$24,$9,$L270
$L40:
	lw	$25,64($sp)
	#nop
	slt	$3,$13,$25
	bne	$3,$0,$L271
$L41:
	sw	$13,104($sp)
	beq	$24,$8,$L272
$L43:
	lw	$14,68($sp)
	#nop
	slt	$15,$13,$14
	bne	$15,$0,$L273
$L44:
	sw	$13,108($sp)
	beq	$7,$24,$L274
$L46:
	lw	$3,72($sp)
	#nop
	slt	$14,$13,$3
	bne	$14,$0,$L275
$L47:
	sw	$13,112($sp)
	beq	$24,$6,$L276
$L49:
	lw	$25,76($sp)
	#nop
	slt	$15,$13,$25
	bne	$15,$0,$L277
$L50:
	sw	$13,116($sp)
	beq	$24,$2,$L278
$L52:
	lw	$14,80($sp)
	#nop
	slt	$25,$13,$14
	bne	$25,$0,$L279
$L53:
	sw	$13,120($sp)
	beq	$5,$24,$L280
$L55:
	lw	$24,84($sp)
	#nop
	slt	$15,$13,$24
	bne	$15,$0,$L281
$L56:
	lb	$25,2($4)
	sw	$13,124($sp)
	beq	$25,$12,$L282
$L58:
	lw	$14,128($sp)
	lw	$13,92($sp)
	#nop
	slt	$24,$13,$14
	bne	$24,$0,$L283
$L59:
	sw	$13,132($sp)
	beq	$25,$11,$L284
$L61:
	lw	$3,96($sp)
	#nop
	slt	$14,$13,$3
	bne	$14,$0,$L285
$L62:
	sw	$13,136($sp)
	beq	$25,$10,$L286
$L64:
	lw	$24,100($sp)
	#nop
	slt	$15,$13,$24
	bne	$15,$0,$L287
$L65:
	sw	$13,140($sp)
	beq	$25,$9,$L288
$L67:
	lw	$14,104($sp)
	#nop
	slt	$24,$13,$14
	bne	$24,$0,$L289
$L68:
	sw	$13,144($sp)
	beq	$25,$8,$L290
$L70:
	lw	$3,108($sp)
	#nop
	slt	$15,$13,$3
	bne	$15,$0,$L291
$L71:
	sw	$13,148($sp)
	beq	$7,$25,$L292
$L73:
	lw	$24,112($sp)
	#nop
	slt	$3,$13,$24
	bne	$3,$0,$L293
$L74:
	sw	$13,152($sp)
	beq	$25,$6,$L294
$L76:
	lw	$14,116($sp)
	#nop
	slt	$15,$13,$14
	bne	$15,$0,$L295
$L77:
	sw	$13,156($sp)
	beq	$25,$2,$L296
$L79:
	lw	$3,120($sp)
	#nop
	slt	$14,$13,$3
	bne	$14,$0,$L297
$L80:
	sw	$13,160($sp)
	beq	$5,$25,$L298
$L82:
	lw	$25,124($sp)
	#nop
	slt	$15,$13,$25
	bne	$15,$0,$L299
$L83:
	lb	$3,3($4)
	sw	$13,164($sp)
	beq	$3,$12,$L300
$L85:
	lw	$14,168($sp)
	lw	$13,132($sp)
	#nop
	slt	$25,$13,$14
	bne	$25,$0,$L301
$L86:
	sw	$13,172($sp)
	beq	$3,$11,$L302
$L88:
	lw	$24,136($sp)
	#nop
	slt	$14,$13,$24
	bne	$14,$0,$L303
$L89:
	sw	$13,176($sp)
	beq	$3,$10,$L304
$L91:
	lw	$25,140($sp)
	#nop
	slt	$15,$13,$25
	bne	$15,$0,$L305
$L92:
	sw	$13,180($sp)
	beq	$3,$9,$L306
$L94:
	lw	$14,144($sp)
	#nop
	slt	$25,$13,$14
	bne	$25,$0,$L307
$L95:
	sw	$13,184($sp)
	beq	$3,$8,$L308
$L97:
	lw	$24,148($sp)
	#nop
	slt	$15,$13,$24
	bne	$15,$0,$L309
$L98:
	sw	$13,188($sp)
	beq	$7,$3,$L310
$L100:
	lw	$25,152($sp)
	#nop
	slt	$24,$13,$25
	bne	$24,$0,$L311
$L101:
	sw	$13,192($sp)
	beq	$3,$6,$L312
$L103:
	lw	$14,156($sp)
	#nop
	slt	$15,$13,$14
	bne	$15,$0,$L313
$L104:
	sw	$13,196($sp)
	beq	$3,$2,$L314
$L106:
	lw	$24,160($sp)
	#nop
	slt	$14,$13,$24
	bne	$14,$0,$L315
$L107:
	sw	$13,200($sp)
	beq	$5,$3,$L316
$L109:
	lw	$3,164($sp)
	#nop
	slt	$15,$13,$3
	bne	$15,$0,$L317
$L110:
	lb	$24,4($4)
	sw	$13,204($sp)
	beq	$24,$12,$L318
$L112:
	lw	$14,208($sp)
	lw	$13,172($sp)
	#nop
	slt	$3,$13,$14
	bne	$3,$0,$L319
$L113:
	sw	$13,212($sp)
	beq	$24,$11,$L320
$L115:
	lw	$25,176($sp)
	#nop
	slt	$14,$13,$25
	bne	$14,$0,$L321
$L116:
	sw	$13,216($sp)
	beq	$24,$10,$L322
$L118:
	lw	$3,180($sp)
	#nop
	slt	$15,$13,$3
	bne	$15,$0,$L323
$L119:
	sw	$13,220($sp)
	beq	$24,$9,$L324
$L121:
	lw	$14,184($sp)
	#nop
	slt	$3,$13,$14
	bne	$3,$0,$L325
$L122:
	sw	$13,224($sp)
	beq	$24,$8,$L326
$L124:
	lw	$25,188($sp)
	#nop
	slt	$15,$13,$25
	bne	$15,$0,$L327
$L125:
	sw	$13,228($sp)
	beq	$7,$24,$L328
$L127:
	lw	$3,192($sp)
	#nop
	slt	$25,$13,$3
	bne	$25,$0,$L329
$L128:
	sw	$13,232($sp)
	beq	$24,$6,$L330
$L130:
	lw	$14,196($sp)
	#nop
	slt	$15,$13,$14
	bne	$15,$0,$L331
$L131:
	sw	$13,236($sp)
	beq	$24,$2,$L332
$L133:
	lw	$25,200($sp)
	#nop
	slt	$14,$13,$25
	bne	$14,$0,$L333
$L134:
	sw	$13,240($sp)
	beq	$5,$24,$L334
$L136:
	lw	$24,204($sp)
	#nop
	slt	$15,$13,$24
	bne	$15,$0,$L335
$L137:
	lb	$25,5($4)
	sw	$13,244($sp)
	beq	$25,$12,$L336
$L139:
	lw	$14,248($sp)
	lw	$13,212($sp)
	#nop
	slt	$24,$13,$14
	bne	$24,$0,$L337
$L140:
	sw	$13,252($sp)
	beq	$25,$11,$L338
$L142:
	lw	$3,216($sp)
	#nop
	slt	$14,$13,$3
	bne	$14,$0,$L339
$L143:
	sw	$13,256($sp)
	beq	$25,$10,$L340
$L145:
	lw	$24,220($sp)
	#nop
	slt	$15,$13,$24
	bne	$15,$0,$L341
$L146:
	sw	$13,260($sp)
	beq	$25,$9,$L342
$L148:
	lw	$14,224($sp)
	#nop
	slt	$24,$13,$14
	bne	$24,$0,$L343
$L149:
	sw	$13,264($sp)
	beq	$25,$8,$L344
$L151:
	lw	$3,228($sp)
	#nop
	slt	$15,$13,$3
	bne	$15,$0,$L345
$L152:
	sw	$13,268($sp)
	beq	$7,$25,$L346
$L154:
	lw	$24,232($sp)
	#nop
	slt	$3,$13,$24
	bne	$3,$0,$L347
$L155:
	sw	$13,272($sp)
	beq	$25,$6,$L348
$L157:
	lw	$14,236($sp)
	#nop
	slt	$15,$13,$14
	bne	$15,$0,$L349
$L158:
	sw	$13,276($sp)
	beq	$25,$2,$L350
$L160:
	lw	$3,240($sp)
	#nop
	slt	$14,$13,$3
	bne	$14,$0,$L351
$L161:
	sw	$13,280($sp)
	beq	$5,$25,$L352
$L163:
	lw	$25,244($sp)
	#nop
	slt	$15,$13,$25
	bne	$15,$0,$L353
$L164:
	lb	$3,6($4)
	sw	$13,284($sp)
	beq	$3,$12,$L354
$L166:
	lw	$14,288($sp)
	lw	$13,252($sp)
	#nop
	slt	$25,$13,$14
	bne	$25,$0,$L355
$L167:
	sw	$13,292($sp)
	beq	$3,$11,$L356
$L169:
	lw	$24,256($sp)
	#nop
	slt	$14,$13,$24
	bne	$14,$0,$L357
$L170:
	sw	$13,296($sp)
	beq	$3,$10,$L358
$L172:
	lw	$25,260($sp)
	#nop
	slt	$15,$13,$25
	bne	$15,$0,$L359
$L173:
	sw	$13,300($sp)
	beq	$3,$9,$L360
$L175:
	lw	$14,264($sp)
	#nop
	slt	$25,$13,$14
	bne	$25,$0,$L361
$L176:
	sw	$13,304($sp)
	beq	$3,$8,$L362
$L178:
	lw	$24,268($sp)
	#nop
	slt	$15,$13,$24
	bne	$15,$0,$L363
$L179:
	sw	$13,308($sp)
	beq	$7,$3,$L364
$L181:
	lw	$25,272($sp)
	#nop
	slt	$24,$13,$25
	bne	$24,$0,$L365
$L182:
	sw	$13,312($sp)
	beq	$3,$6,$L366
$L184:
	lw	$14,276($sp)
	#nop
	slt	$15,$13,$14
	bne	$15,$0,$L367
$L185:
	sw	$13,316($sp)
	beq	$3,$2,$L368
$L187:
	lw	$24,280($sp)
	#nop
	slt	$14,$13,$24
	bne	$14,$0,$L369
$L188:
	sw	$13,320($sp)
	beq	$5,$3,$L370
$L190:
	lw	$3,284($sp)
	#nop
	slt	$15,$13,$3
	bne	$15,$0,$L371
$L191:
	lb	$24,7($4)
	sw	$13,324($sp)
	beq	$24,$12,$L372
$L193:
	lw	$14,328($sp)
	lw	$13,292($sp)
	#nop
	slt	$3,$13,$14
	bne	$3,$0,$L373
$L194:
	sw	$13,332($sp)
	beq	$24,$11,$L374
$L196:
	lw	$25,296($sp)
	#nop
	slt	$14,$13,$25
	bne	$14,$0,$L375
$L197:
	sw	$13,336($sp)
	beq	$24,$10,$L376
$L199:
	lw	$3,300($sp)
	#nop
	slt	$15,$13,$3
	bne	$15,$0,$L377
$L200:
	sw	$13,340($sp)
	beq	$24,$9,$L378
$L202:
	lw	$14,304($sp)
	#nop
	slt	$3,$13,$14
	bne	$3,$0,$L379
$L203:
	sw	$13,344($sp)
	beq	$24,$8,$L380
$L205:
	lw	$25,308($sp)
	#nop
	slt	$15,$13,$25
	bne	$15,$0,$L381
$L206:
	sw	$13,348($sp)
	beq	$7,$24,$L382
$L208:
	lw	$3,312($sp)
	#nop
	slt	$25,$13,$3
	bne	$25,$0,$L383
$L209:
	sw	$13,352($sp)
	beq	$24,$6,$L384
$L211:
	lw	$14,316($sp)
	#nop
	slt	$15,$13,$14
	bne	$15,$0,$L385
$L212:
	sw	$13,356($sp)
	beq	$24,$2,$L386
$L214:
	lw	$25,320($sp)
	#nop
	slt	$14,$13,$25
	bne	$14,$0,$L387
$L215:
	sw	$13,360($sp)
	beq	$5,$24,$L388
$L217:
	lw	$24,324($sp)
	#nop
	slt	$15,$13,$24
	bne	$15,$0,$L389
$L218:
	lb	$25,8($4)
	sw	$13,364($sp)
	beq	$25,$12,$L220
$L407:
	lw	$12,368($sp)
	lw	$3,332($sp)
	#nop
	slt	$14,$3,$12
	bne	$14,$0,$L390
$L222:
	beq	$25,$11,$L223
$L406:
	lw	$11,336($sp)
	#nop
	slt	$24,$3,$11
	bne	$24,$0,$L391
$L225:
	beq	$25,$10,$L226
$L405:
	lw	$10,340($sp)
	#nop
	slt	$13,$3,$10
	bne	$13,$0,$L392
$L228:
	beq	$25,$9,$L229
$L404:
	lw	$9,344($sp)
	#nop
	slt	$12,$3,$9
	bne	$12,$0,$L393
$L231:
	beq	$25,$8,$L232
$L403:
	lw	$8,348($sp)
	#nop
	slt	$11,$3,$8
	bne	$11,$0,$L394
$L234:
	beq	$7,$25,$L235
$L402:
	lw	$7,352($sp)
	#nop
	slt	$24,$3,$7
	bne	$24,$0,$L395
$L237:
	beq	$25,$6,$L238
$L401:
	lw	$6,356($sp)
	#nop
	slt	$10,$3,$6
	bne	$10,$0,$L396
$L240:
	beq	$25,$2,$L241
$L400:
	lw	$9,360($sp)
	move	$2,$3
	slt	$12,$3,$9
	bne	$12,$0,$L397
$L243:
	beq	$5,$25,$L244
$L399:
	lw	$5,364($sp)
	#nop
	slt	$25,$2,$5
	bne	$25,$0,$L398
	addiu	$sp,$sp,408
	j	$31
$L398:
	move	$2,$5
	addiu	$sp,$sp,408
	j	$31
$L397:
	move	$2,$9
	bne	$5,$25,$L399
$L244:
	lw	$14,360($sp)
	addiu	$sp,$sp,408
	addiu	$2,$14,1
	j	$31
$L396:
	move	$3,$6
	bne	$25,$2,$L400
$L241:
	nop				# Y 77
	lw	$2,356($sp)
	#nop
	addiu	$2,$2,1
	b	$L243
$L395:
	move	$3,$7
	bne	$25,$6,$L401
$L238:
	nop				# Y 76
	lw	$13,352($sp)
	#nop
	addiu	$3,$13,1
	b	$L240
$L394:
	move	$3,$8
	bne	$7,$25,$L402
$L235:
	nop				# Y 75
	lw	$15,348($sp)
	#nop
	addiu	$3,$15,1
	b	$L237
$L393:
	move	$3,$9
	bne	$25,$8,$L403
$L232:
	nop				# Y 74
	lw	$4,344($sp)
	#nop
	addiu	$3,$4,1
	b	$L234
$L392:
	move	$3,$10
	bne	$25,$9,$L404
$L229:
	nop				# Y 73
	lw	$14,340($sp)
	#nop
	addiu	$3,$14,1
	b	$L231
$L391:
	move	$3,$11
	bne	$25,$10,$L405
$L226:
	nop				# Y 72
	lw	$3,336($sp)
	#nop
	addiu	$3,$3,1
	b	$L228
$L390:
	move	$3,$12
	bne	$25,$11,$L406
$L223:
	nop				# Y 71
	lw	$15,332($sp)
	#nop
	addiu	$3,$15,1
	b	$L225
$L389:
	lb	$25,8($4)
	move	$13,$24
	sw	$13,364($sp)
	bne	$25,$12,$L407
$L220:
	nop				# Y 70
	lw	$4,328($sp)
	#nop
	addiu	$3,$4,1
	b	$L222
$L387:
	move	$13,$25
	sw	$13,360($sp)
	bne	$5,$24,$L217
$L388:
	nop				# Y 69
	lw	$3,320($sp)
	#nop
	addiu	$13,$3,1
	b	$L218
$L385:
	move	$13,$14
	sw	$13,356($sp)
	bne	$24,$2,$L214
$L386:
	nop				# Y 68
	lw	$13,316($sp)
	#nop
	addiu	$13,$13,1
	b	$L215
$L383:
	move	$13,$3
	sw	$13,352($sp)
	bne	$24,$6,$L211
$L384:
	nop				# Y 67
	lw	$3,312($sp)
	#nop
	addiu	$13,$3,1
	b	$L212
$L381:
	move	$13,$25
	sw	$13,348($sp)
	bne	$7,$24,$L208
$L382:
	nop				# Y 66
	lw	$13,308($sp)
	#nop
	addiu	$13,$13,1
	b	$L209
$L379:
	move	$13,$14
	sw	$13,344($sp)
	bne	$24,$8,$L205
$L380:
	nop				# Y 65
	lw	$14,304($sp)
	#nop
	addiu	$13,$14,1
	b	$L206
$L377:
	move	$13,$3
	sw	$13,340($sp)
	bne	$24,$9,$L202
$L378:
	nop				# Y 64
	lw	$13,300($sp)
	#nop
	addiu	$13,$13,1
	b	$L203
$L375:
	move	$13,$25
	sw	$13,336($sp)
	bne	$24,$10,$L199
$L376:
	nop				# Y 63
	lw	$25,296($sp)
	#nop
	addiu	$13,$25,1
	b	$L200
$L373:
	move	$13,$14
	sw	$13,332($sp)
	bne	$24,$11,$L196
$L374:
	nop				# Y 62
	lw	$13,292($sp)
	#nop
	addiu	$13,$13,1
	b	$L197
$L371:
	lb	$24,7($4)
	move	$13,$3
	sw	$13,324($sp)
	bne	$24,$12,$L193
$L372:
	nop				# Y 61
	lw	$15,288($sp)
	#nop
	addiu	$13,$15,1
	b	$L194
$L369:
	move	$13,$24
	sw	$13,320($sp)
	bne	$5,$3,$L190
$L370:
	nop				# Y 60
	lw	$25,280($sp)
	#nop
	addiu	$13,$25,1
	b	$L191
$L367:
	move	$13,$14
	sw	$13,316($sp)
	bne	$3,$2,$L187
$L368:
	nop				# Y 59
	lw	$13,276($sp)
	#nop
	addiu	$13,$13,1
	b	$L188
$L365:
	move	$13,$25
	sw	$13,312($sp)
	bne	$3,$6,$L184
$L366:
	nop				# Y 58
	lw	$25,272($sp)
	#nop
	addiu	$13,$25,1
	b	$L185
$L363:
	move	$13,$24
	sw	$13,308($sp)
	bne	$7,$3,$L181
$L364:
	nop				# Y 57
	lw	$13,268($sp)
	#nop
	addiu	$13,$13,1
	b	$L182
$L361:
	move	$13,$14
	sw	$13,304($sp)
	bne	$3,$8,$L178
$L362:
	nop				# Y 56
	lw	$14,264($sp)
	#nop
	addiu	$13,$14,1
	b	$L179
$L359:
	move	$13,$25
	sw	$13,300($sp)
	bne	$3,$9,$L175
$L360:
	nop				# Y 55
	lw	$13,260($sp)
	#nop
	addiu	$13,$13,1
	b	$L176
$L357:
	move	$13,$24
	sw	$13,296($sp)
	bne	$3,$10,$L172
$L358:
	nop				# Y 54
	lw	$24,256($sp)
	#nop
	addiu	$13,$24,1
	b	$L173
$L355:
	move	$13,$14
	sw	$13,292($sp)
	bne	$3,$11,$L169
$L356:
	nop				# Y 53
	lw	$13,252($sp)
	#nop
	addiu	$13,$13,1
	b	$L170
$L353:
	lb	$3,6($4)
	move	$13,$25
	sw	$13,284($sp)
	bne	$3,$12,$L166
$L354:
	nop				# Y 52
	lw	$15,248($sp)
	#nop
	addiu	$13,$15,1
	b	$L167
$L351:
	move	$13,$3
	sw	$13,280($sp)
	bne	$5,$25,$L163
$L352:
	nop				# Y 51
	lw	$24,240($sp)
	#nop
	addiu	$13,$24,1
	b	$L164
$L349:
	move	$13,$14
	sw	$13,276($sp)
	bne	$25,$2,$L160
$L350:
	nop				# Y 50
	lw	$13,236($sp)
	#nop
	addiu	$13,$13,1
	b	$L161
$L347:
	move	$13,$24
	sw	$13,272($sp)
	bne	$25,$6,$L157
$L348:
	nop				# Y 49
	lw	$24,232($sp)
	#nop
	addiu	$13,$24,1
	b	$L158
$L345:
	move	$13,$3
	sw	$13,268($sp)
	bne	$7,$25,$L154
$L346:
	nop				# Y 48
	lw	$13,228($sp)
	#nop
	addiu	$13,$13,1
	b	$L155
$L343:
	move	$13,$14
	sw	$13,264($sp)
	bne	$25,$8,$L151
$L344:
	nop				# Y 47
	lw	$14,224($sp)
	#nop
	addiu	$13,$14,1
	b	$L152
$L341:
	move	$13,$24
	sw	$13,260($sp)
	bne	$25,$9,$L148
$L342:
	nop				# Y 46
	lw	$13,220($sp)
	#nop
	addiu	$13,$13,1
	b	$L149
$L339:
	move	$13,$3
	sw	$13,256($sp)
	bne	$25,$10,$L145
$L340:
	nop				# Y 45
	lw	$3,216($sp)
	#nop
	addiu	$13,$3,1
	b	$L146
$L337:
	move	$13,$14
	sw	$13,252($sp)
	bne	$25,$11,$L142
$L338:
	nop				# Y 44
	lw	$13,212($sp)
	#nop
	addiu	$13,$13,1
	b	$L143
$L335:
	lb	$25,5($4)
	move	$13,$24
	sw	$13,244($sp)
	bne	$25,$12,$L139
$L336:
	nop				# Y 43
	lw	$15,208($sp)
	#nop
	addiu	$13,$15,1
	b	$L140
$L333:
	move	$13,$25
	sw	$13,240($sp)
	bne	$5,$24,$L136
$L334:
	nop				# Y 42
	lw	$3,200($sp)
	#nop
	addiu	$13,$3,1
	b	$L137
$L331:
	move	$13,$14
	sw	$13,236($sp)
	bne	$24,$2,$L133
$L332:
	nop				# Y 41
	lw	$13,196($sp)
	#nop
	addiu	$13,$13,1
	b	$L134
$L329:
	move	$13,$3
	sw	$13,232($sp)
	bne	$24,$6,$L130
$L330:
	nop				# Y 40
	lw	$3,192($sp)
	#nop
	addiu	$13,$3,1
	b	$L131
$L327:
	move	$13,$25
	sw	$13,228($sp)
	bne	$7,$24,$L127
$L328:
	nop				# Y 39
	lw	$13,188($sp)
	#nop
	addiu	$13,$13,1
	b	$L128
$L325:
	move	$13,$14
	sw	$13,224($sp)
	bne	$24,$8,$L124
$L326:
	nop				# Y 38
	lw	$14,184($sp)
	#nop
	addiu	$13,$14,1
	b	$L125
$L323:
	move	$13,$3
	sw	$13,220($sp)
	bne	$24,$9,$L121
$L324:
	nop				# Y 37
	lw	$13,180($sp)
	#nop
	addiu	$13,$13,1
	b	$L122
$L321:
	move	$13,$25
	sw	$13,216($sp)
	bne	$24,$10,$L118
$L322:
	nop				# Y 36
	lw	$25,176($sp)
	#nop
	addiu	$13,$25,1
	b	$L119
$L319:
	move	$13,$14
	sw	$13,212($sp)
	bne	$24,$11,$L115
$L320:
	nop				# Y 35
	lw	$13,172($sp)
	#nop
	addiu	$13,$13,1
	b	$L116
$L317:
	lb	$24,4($4)
	move	$13,$3
	sw	$13,204($sp)
	bne	$24,$12,$L112
$L318:
	nop				# Y 34
	lw	$15,168($sp)
	#nop
	addiu	$13,$15,1
	b	$L113
$L315:
	move	$13,$24
	sw	$13,200($sp)
	bne	$5,$3,$L109
$L316:
	nop				# Y 33
	lw	$25,160($sp)
	#nop
	addiu	$13,$25,1
	b	$L110
$L313:
	move	$13,$14
	sw	$13,196($sp)
	bne	$3,$2,$L106
$L314:
	nop				# Y 32
	lw	$13,156($sp)
	#nop
	addiu	$13,$13,1
	b	$L107
$L311:
	move	$13,$25
	sw	$13,192($sp)
	bne	$3,$6,$L103
$L312:
	nop				# Y 31
	lw	$25,152($sp)
	#nop
	addiu	$13,$25,1
	b	$L104
$L309:
	move	$13,$24
	sw	$13,188($sp)
	bne	$7,$3,$L100
$L310:
	nop				# Y 30
	lw	$13,148($sp)
	#nop
	addiu	$13,$13,1
	b	$L101
$L307:
	move	$13,$14
	sw	$13,184($sp)
	bne	$3,$8,$L97
$L308:
	nop				# Y 29
	lw	$14,144($sp)
	#nop
	addiu	$13,$14,1
	b	$L98
$L305:
	move	$13,$25
	sw	$13,180($sp)
	bne	$3,$9,$L94
$L306:
	nop				# Y 28
	lw	$13,140($sp)
	#nop
	addiu	$13,$13,1
	b	$L95
$L303:
	move	$13,$24
	sw	$13,176($sp)
	bne	$3,$10,$L91
$L304:
	nop				# Y 27
	lw	$24,136($sp)
	#nop
	addiu	$13,$24,1
	b	$L92
$L301:
	move	$13,$14
	sw	$13,172($sp)
	bne	$3,$11,$L88
$L302:
	nop				# Y 79
	lw	$13,132($sp)
	#nop
	addiu	$13,$13,1
	b	$L89
$L299:
	lb	$3,3($4)
	move	$13,$25
	sw	$13,164($sp)
	bne	$3,$12,$L85
$L300:
	nop				# Y 78
	lw	$15,128($sp)
	#nop
	addiu	$13,$15,1
	b	$L86
$L297:
	move	$13,$3
	sw	$13,160($sp)
	bne	$5,$25,$L82
$L298:
	nop				# Y 26
	lw	$24,120($sp)
	#nop
	addiu	$13,$24,1
	b	$L83
$L295:
	move	$13,$14
	sw	$13,156($sp)
	bne	$25,$2,$L79
$L296:
	nop				# Y 25
	lw	$13,116($sp)
	#nop
	addiu	$13,$13,1
	b	$L80
$L293:
	move	$13,$24
	sw	$13,152($sp)
	bne	$25,$6,$L76
$L294:
	nop				# Y 24
	lw	$24,112($sp)
	#nop
	addiu	$13,$24,1
	b	$L77
$L291:
	move	$13,$3
	sw	$13,148($sp)
	bne	$7,$25,$L73
$L292:
	nop				# Y 23
	lw	$13,108($sp)
	#nop
	addiu	$13,$13,1
	b	$L74
$L289:
	move	$13,$14
	sw	$13,144($sp)
	bne	$25,$8,$L70
$L290:
	nop				# Y 22
	lw	$14,104($sp)
	#nop
	addiu	$13,$14,1
	b	$L71
$L287:
	move	$13,$24
	sw	$13,140($sp)
	bne	$25,$9,$L67
$L288:
	nop				# Y 21
	lw	$13,100($sp)
	#nop
	addiu	$13,$13,1
	b	$L68
$L285:
	move	$13,$3
	sw	$13,136($sp)
	bne	$25,$10,$L64
$L286:
	nop				# Y 20
	lw	$3,96($sp)
	#nop
	addiu	$13,$3,1
	b	$L65
$L283:
	move	$13,$14
	sw	$13,132($sp)
	bne	$25,$11,$L61
$L284:
	nop				# Y 19
	lw	$13,92($sp)
	#nop
	addiu	$13,$13,1
	b	$L62
$L281:
	lb	$25,2($4)
	move	$13,$24
	sw	$13,124($sp)
	bne	$25,$12,$L58
$L282:
	nop				# Y 18
	lw	$15,88($sp)
	#nop
	addiu	$13,$15,1
	b	$L59
$L279:
	move	$13,$14
	sw	$13,120($sp)
	bne	$5,$24,$L55
$L280:
	nop				# Y 17
	lw	$3,80($sp)
	#nop
	addiu	$13,$3,1
	b	$L56
$L277:
	move	$13,$25
	sw	$13,116($sp)
	bne	$24,$2,$L52
$L278:
	nop				# Y 16
	lw	$13,76($sp)
	#nop
	addiu	$13,$13,1
	b	$L53
$L275:
	move	$13,$3
	sw	$13,112($sp)
	bne	$24,$6,$L49
$L276:
	nop				# Y 15
	lw	$3,72($sp)
	#nop
	addiu	$13,$3,1
	b	$L50
$L273:
	move	$13,$14
	sw	$13,108($sp)
	bne	$7,$24,$L46
$L274:
	nop				# Y 14
	lw	$13,68($sp)
	#nop
	addiu	$13,$13,1
	b	$L47
$L271:
	move	$13,$25
	sw	$13,104($sp)
	bne	$24,$8,$L43
$L272:
	nop				# Y 13
	lw	$25,64($sp)
	#nop
	addiu	$13,$25,1
	b	$L44
$L269:
	move	$13,$3
	sw	$13,100($sp)
	bne	$24,$9,$L40
$L270:
	nop				# Y 12
	lw	$13,60($sp)
	#nop
	addiu	$13,$13,1
	b	$L41
$L267:
	move	$13,$14
	sw	$13,96($sp)
	bne	$24,$10,$L37
$L268:
	nop				# Y 11
	lw	$14,56($sp)
	#nop
	addiu	$13,$14,1
	b	$L38
$L265:
	move	$13,$25
	sw	$13,92($sp)
	bne	$24,$11,$L34
$L266:
	nop				# Y 10
	lw	$13,52($sp)
	#nop
	addiu	$13,$13,1
	b	$L35
$L263:
	lb	$24,1($4)
	move	$13,$3
	sw	$13,84($sp)
	bne	$24,$12,$L31
$L264:
	nop				# Y 9
	lw	$15,48($sp)
	#nop
	addiu	$13,$15,1
	b	$L32
$L261:
	move	$13,$24
	sw	$13,80($sp)
	bne	$5,$3,$L28
$L262:
	nop				# Y 8
	lw	$14,40($sp)
	#nop
	addiu	$13,$14,1
	b	$L29
$L259:
	move	$13,$25
	sw	$13,76($sp)
	bne	$3,$2,$L25
$L260:
	nop				# Y 7
	lw	$13,36($sp)
	#nop
	addiu	$13,$13,1
	b	$L26
$L257:
	move	$13,$14
	sw	$13,72($sp)
	bne	$3,$6,$L22
$L258:
	nop				# Y 6
	lw	$14,32($sp)
	#nop
	addiu	$13,$14,1
	b	$L23
$L255:
	move	$13,$24
	sw	$13,68($sp)
	bne	$7,$3,$L19
$L256:
	nop				# Y 5
	lw	$13,28($sp)
	#nop
	addiu	$13,$13,1
	b	$L20
$L253:
	move	$13,$25
	sw	$13,64($sp)
	bne	$3,$8,$L16
$L254:
	nop				# Y 4
	lw	$25,24($sp)
	#nop
	addiu	$13,$25,1
	b	$L17
$L251:
	move	$13,$14
	sw	$13,60($sp)
	bne	$3,$9,$L13
$L252:
	nop				# Y 3
	lw	$13,20($sp)
	#nop
	addiu	$13,$13,1
	b	$L14
$L250:
	nop				# Y 2
	lw	$24,16($sp)
	#nop
	addiu	$13,$24,1
	b	$L11
$L249:
	li	$13,1			# 0x1
	b	$L8
	.end	sfe_main
	.size	sfe_main, .-sfe_main
	.ident	"GCC: (GNU) 4.7.3"
