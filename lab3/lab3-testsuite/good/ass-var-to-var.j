.class public ass-var-to-var
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

  invokestatic ass-var-to-var/main()I
  pop
  return

.end method


.method public static main()I
  .limit locals 2
  .limit stack 2

  iconst_0
  istore_0
  iload_0
  istore_1
  iconst_0
  ireturn

  ;; default return
iconst_0
ireturn

.end method
