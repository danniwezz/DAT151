.class public core007
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

  invokestatic core007/main()I
  pop
  return

.end method


.method public static main()I
  .limit locals 1
  .limit stack 1

  bipush 7
  istore_0
  iload_0
  invokestatic Runtime/printInt(I)V
  iconst_0
  ireturn

  ;; default return
iconst_0
ireturn

.end method
