.class public separate-namespaces
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

  invokestatic separate-namespaces/main()I
  pop
  return

.end method


.method public static x()I
  .limit locals 0
  .limit stack 1

  iconst_0
  ireturn

.end method

.method public static main()I
  .limit locals 1
  .limit stack 1

  iconst_5
  istore_0
  iload_0
  ireturn

  ;; default return
iconst_0
ireturn

.end method
