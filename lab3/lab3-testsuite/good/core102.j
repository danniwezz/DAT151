.class public core102
.super java/lang/Object

.method public <init>()V
  .limit locals 1

  aload_0
  invokespecial java/lang/Object/<init>()V
  return

.end method

.method public static main([Ljava/lang/String;)V
  .limit locals 1
  .limit stack  1

  invokestatic core102/main()I
  pop
  return

.end method


.method public static main()I
  .limit locals 0
  .limit stack 4

  L0:
  iconst_2
  iconst_5
  bipush 6
  imul
  iconst_5
  idiv
  iadd
  bipush 67
  isub
  iconst_5
  if_icmpgt L4
  iconst_0
  if_icmpeq L0
  goto L0
  L1:
  iconst_0
  ireturn

  ;; default return
iconst_0
ireturn

.end method
