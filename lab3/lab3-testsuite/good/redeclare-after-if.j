.class public redeclare-after-if
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

  invokestatic redeclare-after-if/main()I
  pop
  return

.end method


.method public static main()I
  .limit locals 2
  .limit stack 5

  iconst_0
  iconst_2
  if_icmple L4
  iconst_0
  iconst_1
  istore_0
  goto L1
  iconst_0
  L1:
  iconst_0
  istore_1
  iconst_1
  invokestatic Runtime/printInt(I)V
  iconst_0
  ireturn

  ;; default return
iconst_0
ireturn

.end method
